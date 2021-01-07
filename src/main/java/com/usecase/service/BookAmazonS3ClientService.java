package com.usecase.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.io.Files;
import com.usecase.entity.Book;
import com.usecase.AmazonS3Config;

@Service
public class BookAmazonS3ClientService {
	
	private Logger logger = LoggerFactory.getLogger(BookAmazonS3ClientService.class);
	
	@Autowired
	private AmazonS3 s3Client;
	
	@Autowired
	private AmazonS3Config amazons3Config;
	
	@SuppressWarnings("unchecked")
	public List<Book> readBooksFromS3() {
		List<Book> books = new ArrayList<>();
		try {
			if (s3Client.doesObjectExist(amazons3Config.getAWSS3AudioBucket(), "books.txt")) {
				S3Object s3object = s3Client.getObject(amazons3Config.getAWSS3AudioBucket(), "books.txt");
				ObjectInputStream inputStream = new ObjectInputStream(s3object.getObjectContent());
				books = (List<Book>) inputStream.readObject();
				inputStream.close();
			}
		} catch(IOException | ClassNotFoundException e) {
			logger.error("Error While reading books from s3 to local");
		}
		return books;
	}
	
	public void saveBookRecordsToS3(List<Book> books) {
		File booksFile = new File(getFullFilePath("books.txt"));
		try (FileOutputStream fileOutputStream = new FileOutputStream(booksFile);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
			Files.touch(booksFile);
			objectOutputStream.writeObject(books);
			uploadFile("books.txt", booksFile);
		} catch (IOException e) {
			logger.error("Error While writing books list to s3");
		}
	}
	
	public synchronized long getID() {
		long id = 1;
		File idFile = new File(getFullFilePath("last-book-id.txt"));
		try (FileOutputStream outputStream = new FileOutputStream(idFile)) {
			Files.touch(idFile);
			if (s3Client.doesObjectExist(amazons3Config.getAWSS3AudioBucket(), "last-book-id.txt")) {
				S3Object s3object = s3Client.getObject(amazons3Config.getAWSS3AudioBucket(), "last-book-id.txt");
				String idString = IOUtils.toString(s3object.getObjectContent(), StandardCharsets.UTF_8);
				id = Long.parseLong(idString) + 1;
			}
			outputStream.write(String.valueOf(id).getBytes());
			uploadFile("last-book-id.txt", idFile);
		} catch (IOException e) {
			logger.error("Error Occurred while reading/writing the id file");
		}
		return id;
	}
	
	private void uploadFile(String fileName, File file) {
		PutObjectRequest putObjectRequest = new PutObjectRequest(amazons3Config.getAWSS3AudioBucket(), fileName, file);
		s3Client.putObject(putObjectRequest);
	}

	private String getFullFilePath(String fileName) {
		StringBuilder builder = new StringBuilder();
		builder.append(amazons3Config.getLocalFolderPath());
		builder.append("/");
		builder.append(fileName);
		return builder.toString();
	}
}
