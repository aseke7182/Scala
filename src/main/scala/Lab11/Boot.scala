package Lab11

import java.io.{BufferedReader, File, InputStreamReader}

import akka.pattern.ask
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amazonaws.services.s3.model.{GetObjectRequest, ListObjectsV2Request, ListObjectsV2Result, ObjectMetadata, PutObjectRequest}
import com.sun.corba.se.spi.ior.ObjectKey
import scala.concurrent.duration._

import collection.JavaConverters._
import Lab11.actors._
import Lab11.models.{ErrorResponse, SuccessfulResponse}
import akka.http.scaladsl.Http

import scala.concurrent.ExecutionContextExecutor

object Boot extends App with SprayJsonSerializer {

  implicit val system = ActorSystem("path-service")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit val timeout = Timeout(10.seconds)

  val clientRegion = Regions.EU_CENTRAL_1

  val credentials = new BasicAWSCredentials("AKIA45RMTPTN2RCBG4VO","QacdXT11RtQGTAygp6wbhv4GGdbZscC/WgdJwaeu" )

  val client: AmazonS3 = AmazonS3ClientBuilder.standard()
    .withCredentials(new AWSStaticCredentialsProvider(credentials))
    .withRegion(clientRegion)
    .build()

  val bucketName: String = "task1-kbtu"
  createBucket()

  val actor = system.actorOf(PathManager.props(client,bucketName),"kbtu")

  val route = path("check"){
    get{
      complete {
        "OK"
      }
    }
  } ~
  path("file"){
    concat(
      get{
        parameters('filename.as[String]) { fileName =>
          complete{
            (actor ? PathManager.GetPath(fileName)).mapTo[Either[ErrorResponse,SuccessfulResponse]]
          }
        }
      }~
      post {
        parameters('filename.as[String]) { fileName =>
          complete {
            (actor ? PathManager.PostPath(fileName)).mapTo[Either[ErrorResponse,SuccessfulResponse]]
          }
        }
      }
    )
  }




  def createBucket() = {
    if(!client.doesBucketExistV2(bucketName))
      client.createBucket(bucketName)
      val location = client.getBucketLocation(bucketName)
  }

  val bindingFuture = Http().bindAndHandle(route,"0.0.0.0", 8080)
}
