/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package main.java.recognizer;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.Property;
import com.google.api.services.vision.v1.model.Status;
import com.google.api.services.vision.v1.model.Vertex;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.tokenize.TokenizerME;

import redis.clients.jedis.JedisPool;

import java.awt.Color;
import java.io.Console;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A sample application that uses the Vision API to OCR text in an image.
 */
@SuppressWarnings("serial")
public class TextApp {
	private static final int MAX_RESULTS = 20;
	private static final int BATCH_SIZE = 20;

	/**
	 * Be sure to specify the name of your application. If the application name
	 * is {@code null} or blank, the application will log a warning. Suggested
	 * format is "MyCompany-ProductName/1.0".
	 */
	private static final String APPLICATION_NAME = "Google-VisionTextSample/1.0";

	/**
	 * Connects to the Vision API using Application Default Credentials.
	 */
	public static Vision getVisionService() throws IOException, GeneralSecurityException {
		GoogleCredential credential = GoogleCredential.getApplicationDefault().createScoped(VisionScopes.all());
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
				.setApplicationName(APPLICATION_NAME).build();
	}

	/**
	 * Annotates an image using the Vision API.
	 */
	public static void main(String[] args) throws IOException, GeneralSecurityException {
		if (args.length > 1) {
			System.err.println("Usage:");
			System.err.printf("\tjava %s inputDirectory\n", TextApp.class.getCanonicalName());
			System.exit(1);
		}

		JedisPool pool = Index.getJedisPool();
		try {
			Index index = new Index(new TokenizerME(Index.getEnglishTokenizerMeModel()),
					new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH), pool);
			TextApp app = new TextApp(TextApp.getVisionService(), index);

			if (args.length == 0) {
				app.lookupWords();
				return;
			}
			Path inputPath = Paths.get(args[0]);
			app.indexDirectory(inputPath);
		} finally {
			if (pool != null) {
				pool.destroy();
			}
		}
	}

	private final Vision vision;
	private final Index index;

	/**
	 * Constructs a {@code TextApp} using the {@link Vision} service.
	 */
	public TextApp(Vision vision, Index index) {
		this.vision = vision;
		this.index = index;
	}

	/**
	 * Looks up words in the index that the user enters into the console.
	 */
	public void lookupWords() {
		System.out.println("Entering word lookup mode.");
		System.out.println("To index a directory, add an input path argument when you run this command.");
		System.out.println();

		Console console = System.console();
		if (console == null) {
			System.err.println("No console.");
			System.exit(1);
		}

		while (true) {
			String words = console.readLine("Enter word(s) (comma-separated, leave blank to exit): ").trim();
			if (words.equals("")) {
				break;
			}
			index.printLookup(Splitter.on(',').split(words));
		}
	}

	/**
	 * Indexes all the images in the {@code inputPath} directory for text.
	 */
	public void indexDirectory(Path inputPath) throws IOException {
		List<Path> unprocessedImages = Files.walk(inputPath).filter(Files::isRegularFile)
				.filter(index::isDocumentUnprocessed).collect(Collectors.toList());
		Lists.<Path>partition(unprocessedImages, BATCH_SIZE).stream().map(this::detectText).flatMap(l -> l.stream())
				.filter(this::successfullyDetectedText).map(this::extractDescriptions).forEach(index::addDocument);
	}

	public void printRecognizedWords(List<EntityAnnotation> entityAnnotationList) {
		for (EntityAnnotation entityAnnotation : entityAnnotationList) {
			// affichage resultat du contenue de l'image
			System.out.print(entityAnnotation.getDescription());
			List<Vertex> getVer;
			getVer = entityAnnotation.getBoundingPoly().getVertices();
			//System.out.println(getVer);
			/*
			 * System.out.println(getVer.get(0).getY());
			 * System.out.println(getVer.get(1).getY());
			 * System.out.println(getVer.get(2).getY());
			 * System.out.println(getVer.get(3).getY());
			 */			
			for (int i = 0; i < getVer.size(); i++) {
				  for (int j = i+1; j < getVer.size(); j++) {
				    Integer x0 = getVer.get(0).getY();
					Integer x1 = getVer.get(1).getY();
					// compare list.get(i) and list.get(j)
					if (x0.valueOf(i).equals(x0.valueOf(j))) {
						System.out.println(x0);
					}
				  }
				}
		}
		System.out.print("\n************************************************************\n");
	}

	public void drawAnnotatedImage(List<EntityAnnotation> entityAnnotationList, Path path) {
		TextToGraphics.textToImage(entityAnnotationList, path);
	}

	/**
	 * Gets up to {@code maxResults} text annotations for images stored at
	 * {@code paths}.
	 */
	public ImmutableList<ImageText> detectText(List<Path> paths) {
		ImmutableList.Builder<AnnotateImageRequest> requests = ImmutableList.builder();
		try {
			for (Path path : paths) {
				byte[] data;
				data = Files.readAllBytes(path);
				requests.add(new AnnotateImageRequest().setImage(new Image().encodeContent(data)).setFeatures(
						ImmutableList.of(new Feature().setType("TEXT_DETECTION").setMaxResults(MAX_RESULTS))));
			}

			Vision.Images.Annotate annotate = vision.images()
					.annotate(new BatchAnnotateImagesRequest().setRequests(requests.build()));
			// Due to a bug: requests to Vision API containing large images fail
			// when GZipped.
			annotate.setDisableGZipContent(true);
			BatchAnnotateImagesResponse batchResponse = annotate.execute();
			assert batchResponse.getResponses().size() == paths.size();

			ImmutableList.Builder<ImageText> output = ImmutableList.builder();
			for (int i = 0; i < paths.size(); i++) {
				Path path = paths.get(i);
				AnnotateImageResponse response = batchResponse.getResponses().get(i);
				recognizeAmounts(response.getTextAnnotations());
				printRecognizedWords(response.getTextAnnotations());
				drawAnnotatedImage(response.getTextAnnotations(), path);
				output.add(ImageText.builder().path(path).textAnnotations(
						MoreObjects.firstNonNull(response.getTextAnnotations(), ImmutableList.<EntityAnnotation>of()))
						.error(response.getError()).build());
			}
			return output.build();
		} catch (IOException ex) {
			// Got an exception, which means the whole batch had an error.
			ImmutableList.Builder<ImageText> output = ImmutableList.builder();
			for (Path path : paths) {
				output.add(ImageText.builder().path(path).textAnnotations(ImmutableList.<EntityAnnotation>of())
						.error(new Status().setMessage(ex.getMessage())).build());
			}
			return output.build();
		}
	}

	private void recognizeAmounts(List<EntityAnnotation> textAnnotations) {
		// Skip first containing hole bill
		textAnnotations.remove(textAnnotations.size() - 1);
		for (EntityAnnotation entityAnnotationSubject : textAnnotations) {
			double minDistance = 0;
			List<Vertex> subjectVertices = entityAnnotationSubject.getBoundingPoly().getVertices();
			float subjectXCenter = subjectVertices.get(1).getX();
			float subjectYCenter = subjectVertices.get(1).getY()
					+ (subjectVertices.get(2).getY() - subjectVertices.get(1).getY()) / 2;
			List<Property> properties = new ArrayList<Property>();
			EntityAnnotation nearestEntity = null;
			for (EntityAnnotation entityAnnotation : textAnnotations) {
				try {
					float amount = Float.parseFloat(entityAnnotation.getDescription());
					List<Vertex> currentVertices = entityAnnotation.getBoundingPoly().getVertices();
					float currentXCenter = currentVertices.get(0).getX();
					float currentYCenter = subjectVertices.get(0).getY() + currentVertices.get(3).getY()
							- subjectVertices.get(0).getY() / 2;
					double currentDistance = Math.sqrt(Math.pow((currentXCenter - subjectXCenter), 2)
							+ Math.pow((currentYCenter - subjectYCenter), 2));
					if (entityAnnotationSubject.getDescription().equals("TOTAL")) {
						// affichage description d'organisation 01: 100.0 |
						// 101.0
						// System.out.println(entityAnnotation.getDescription()
						// + ": " + currentXCenter + " |" + currentYCenter);
					}
					if ((currentXCenter > subjectXCenter && currentYCenter > subjectYCenter)
							&& (currentDistance < minDistance || minDistance == 0)) {
						minDistance = currentDistance;
						nearestEntity = entityAnnotation;
					}
				} catch (NumberFormatException e) {
				}

			}
			if (entityAnnotationSubject.getDescription().equals("MONTANT")
					|| entityAnnotationSubject.getDescription().equals("TOTAL")) {
				try {
					// affichage coordonnées
					System.out.println(entityAnnotationSubject.getDescription() + ": "
							+ entityAnnotationSubject.getBoundingPoly().toPrettyString());
				} catch (IOException e) {
					e.printStackTrace();
				}
				Property property = new Property();
				property.setName("AMOUNT");
				properties.add(property);
				if (nearestEntity != null) {
					property.setValue(nearestEntity.getDescription());
					properties.add(property);
					System.out.println("*********Total Facture : " + nearestEntity.getDescription() + "**********");
				}
			}
			entityAnnotationSubject.setProperties(properties);
		}

	}

	/**
	 * Checks that there was not an error processing an {@code image}.
	 */
	public boolean successfullyDetectedText(ImageText image) {
		if (image.error() != null) {
			System.out.printf("Error reading %s:\n%s\n", image.path(), image.error().getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Extracts as a combinded string, all the descriptions from text
	 * annotations on an {@code image}.
	 */
	public Word extractDescriptions(ImageText image) {
		String document = "";
		for (EntityAnnotation text : image.textAnnotations()) {
			document += text.getDescription();
		}
		if (document.equals("")) {
			System.out.printf("%s had no discernible text.\n", image.path());
		}
		// Output a progress indicator.
		System.out.print('.');
		System.out.flush();
		return Word.builder().path(image.path()).word(document).build();
	}
}
