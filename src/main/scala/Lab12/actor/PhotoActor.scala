package actor

import java.io.{File, InputStream}

import Lab12.model.{ErrorResponse, PhotoResponse, SuccessfulResponse}
import akka.actor.{Actor, ActorLogging, Props}
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.{GetObjectRequest, ObjectMetadata, PutObjectRequest}

object PhotoActor {
  case class UploadPhoto(inputStream: InputStream, fileName: String, contentType: String)
  case class GetPhoto(fileName: String)

  def props(client: AmazonS3, bucketName: String) = Props(new PhotoActor(client, bucketName))
}

class PhotoActor(client: AmazonS3, bucketName: String) extends Actor with ActorLogging {
  import PhotoActor._

  override def receive: Receive = {
    case UploadPhoto(inputStream, fileName, contentType) =>
      // Upload a file as a new object with ContentType and title specified.
      val metadata = new ObjectMetadata()

      val key = s"photos/$fileName"

      metadata.setContentType(contentType)
      val request = new PutObjectRequest(bucketName, key, inputStream, metadata)
      val result = client.putObject(request)

      sender() ! SuccessfulResponse(201, s"file version: ${result.getVersionId}")

      log.info("Successfully put object with filename: {} to AWS S3", fileName)

      context.stop(self)

    case GetPhoto(fileName) => {
      val sender1 = sender()
      val fullPath = s"photos/${fileName}"
      log.info(fullPath)
      if(client.doesObjectExist(bucketName,fullPath)){
        log.info("asdasdasd")
        val content = client.getObject(new GetObjectRequest(bucketName,fullPath)).getObjectContent
        val array: Array[Byte] = Stream.continually(content.read).takeWhile( _ != -1 ).map( _.toByte).toArray
        sender1 ! Right(PhotoResponse(200,array))
      }else{
        sender1 ! Left(ErrorResponse(404,"failed"))
      }
      context.stop(self)

    }
  }

}