package fi.metatavu.metamind.files;

import com.amazonaws.services.s3.model.*;
import org.apache.commons.lang3.StringUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

public class AwsFileController implements FileController {
  private String region;
  private String bucket;
  private String prefix;
  private String folder;

  public AwsFileController() throws FileStorageException {
    region = System.getenv("S3_FILE_STORAGE_REGION");
    bucket = System.getenv("S3_FILE_STORAGE_BUCKET");
    prefix = System.getenv("S3_FILE_STORAGE_PREFIX");
    folder = System.getenv("S3_FILE_FOLDER");

    if (StringUtils.isBlank(region)) {
      throw new FileStorageException("S3_FILE_STORAGE_REGION is not set");
    }

    if (StringUtils.isBlank(bucket)) {
      throw new FileStorageException("S3_FILE_STORAGE_BUCKET is not set");
    }

    if (StringUtils.isBlank(prefix)) {
      throw new FileStorageException("S3_FILE_STORAGE_PREFIX is not set");
    }

    if (StringUtils.isBlank(folder)) {
      throw new FileStorageException("S3_FILE_STORAGE_FOLDER is not set");
    }

    if (!getClient().doesBucketExistV2(bucket)) {
      throw new FileStorageException("Bucket " + bucket + " does not exist!");
    }
  }

  @Override
  public InputStream getFile(String fileName) {
    return getClient().getObject(new GetObjectRequest(bucket, fileName)).getObjectContent();
  }

  @Override
  public void deleteFile(String fileName) {
    getClient().deleteObject(new DeleteObjectRequest(bucket, fileName));
  }

  @Override
  public void addFile(InputStream file, String fileName, String contentType) {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType(contentType);
    metadata.addUserMetadata("x-file-name", fileName);
    getClient().putObject(new PutObjectRequest(bucket, String.format("%s/%s", folder, fileName), file, metadata).withCannedAcl(CannedAccessControlList.PublicRead));
  }

  /**
   * Returns initialized S3 client
   *
   * @return initialized S3 client
   */
  private AmazonS3 getClient() {
    return AmazonS3ClientBuilder.standard().withRegion(region).build();
  }
}
