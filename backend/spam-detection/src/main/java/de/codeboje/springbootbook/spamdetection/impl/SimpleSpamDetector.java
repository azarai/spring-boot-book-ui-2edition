package de.codeboje.springbootbook.spamdetection.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import de.codeboje.springbootbook.spamdetection.SpamDetector;

/**
 * Simple Spam Detector - checks for unwanted words.
 * 
 */
public class SimpleSpamDetector implements SpamDetector {

	private List<String> spamWords = new ArrayList<String>();

	public SimpleSpamDetector(String filename) throws IOException {
		this.spamWords = Files.readAllLines(new File(filename).toPath());
	}

	@Override
	public boolean containsSpam(String value) {

		for (String spam : spamWords) {
			if (value.toLowerCase().contains(spam)) {
				return true;
			}
		}
		return false;
	}

}
