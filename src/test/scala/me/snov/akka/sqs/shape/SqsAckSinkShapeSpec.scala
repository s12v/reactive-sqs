package me.snov.akka.sqs.shape

import akka.stream.scaladsl.Sink
import akka.stream.testkit.scaladsl.TestSource
import com.amazonaws.services.sqs.model.Message
import me.snov.akka.sqs._
import me.snov.akka.sqs.client.SqsClient
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar.mock
import org.scalatest.{FlatSpec, Matchers}

class SqsAckSinkShapeSpec extends FlatSpec with Matchers with DefaultTestContext {
  it should "delete messages on Ack" in {

    val sqsClient = mock[SqsClient]
    val message = new Message().withMessageId("foo")

    TestSource.probe[MessageActionPair]
      .to(Sink.fromGraph(SqsAckSinkShape(sqsClient)))
      .run()
      .sendNext((message, Ack()))

    Thread.sleep(100)

    verify(sqsClient, times(1)).deleteAsync(message)
  }

  it should "requeue messages on RequeueWithDelay" in {

    val sqsClient = mock[SqsClient]
    val message = new Message().withMessageId("foo")

    TestSource.probe[MessageActionPair]
      .to(Sink.fromGraph(SqsAckSinkShape(sqsClient)))
      .run()
      .sendNext((message, RequeueWithDelay(9)))

    Thread.sleep(100)

    verify(sqsClient, times(1)).requeueWithDelayAsync(message, 9)
  }

}