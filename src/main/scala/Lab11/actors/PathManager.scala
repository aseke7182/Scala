package Lab11.actors

import java.io.{File, FilenameFilter}
import java.nio.file.Paths

import akka.actor.{Actor, ActorLogging, Props}
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.{GetObjectRequest, ListObjectsRequest, ObjectMetadata, PutObjectRequest, PutObjectResult}
import Lab11.models._
import better.files._
import com.sun.corba.se.spi.ior.ObjectKey

import scala.util.{Failure, Success, Try}

object  PathManager {

  case class GetPath(path: String)
  case class PostPath(path: String)

  def props(client: AmazonS3, bucketName: String) = Props( new PathManager(client,bucketName))
}

class PathManager(client: AmazonS3, bucketName: String) extends Actor with ActorLogging {
  import PathManager._

  val ourPath = "./src/main/resources/s3"

  def downloadFile(client: AmazonS3, bucketName: String, objectKey: String, path: String) = {
    val index = path.lastIndexOf("/")
    val file1 = path.substring(0,index).toFile.createIfNotExists(true,true)
    val file2 = path.toFile.createIfNotExists()
    val file = new File(path)
    client.getObject(new GetObjectRequest(bucketName, objectKey), file)
  }

  def uploadFile(client: AmazonS3, bucketName: String, objectKey: String, path: String) = {
    val metadata = new ObjectMetadata()
    metadata.setContentType("plain/text")
    metadata.addUserMetadata("user-type","customer")

    val request = new PutObjectRequest(bucketName,objectKey,new File(path))
    request.setMetadata(metadata)
    client.putObject(request)
  }



  override def receive: Receive = {

    case GetPath(totalPath) => {
      val sender1 = sender()
      val objectKey = totalPath
      if (client.doesObjectExist(bucketName, objectKey)) {
        val fullPath = s"${ourPath}/${objectKey}"
        downloadFile(client, bucketName, objectKey, fullPath)
        sender1 ! Right(SuccessfulResponse(200, "File downloaded"))
      } else {
        sender1 ! Left(ErrorResponse(404, "Not Found"))
      }
    }

    case PostPath(totalPath) => {
      val sender1 = sender()
      val objectKey = totalPath
      if(client.doesObjectExist(bucketName,objectKey)){
        sender1 ! Left(ErrorResponse(409,"File already in bucket"))
      }else{
        val fullPath = s"${ourPath}/${objectKey}"
        Try(uploadFile(client,bucketName,objectKey,fullPath)) match {
          case Success(value) =>
            sender1 ! Right(SuccessfulResponse(201,"uploaded"))
          case Failure(exception) =>
            sender1 ! Left(ErrorResponse(500,"Error"))
        }
      }
    }
  }
}
