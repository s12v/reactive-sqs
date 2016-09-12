package me.snov.akka.sqs.source

import akka.stream._
import akka.stream.stage.GraphStage
import me.snov.akka.sqs.SqsMessage
import me.snov.akka.sqs.client.{SqsClient, SqsClientSettings}

object SqsSourceShape {
  def apply(settings: SqsClientSettings): SqsSourceShape = apply(SqsClient(settings))

  def apply(client: SqsClient): SqsSourceShape = new SqsSourceShape(client)
}

class SqsSourceShape(client: SqsClient) extends GraphStage[SourceShape[SqsMessage]] {
  // Define the (sole) output port of this stage
  val out: Outlet[SqsMessage] = Outlet("SqsMessageOut")

  // Define the shape of this stage, which is SourceShape with the port we defined above
  override val shape: SourceShape[SqsMessage] = SourceShape(out)

  // This is where the actual (possibly stateful) logic will live
  override def createLogic(inheritedAttributes: Attributes) = new SqsSourceGraphStageLogic(client, out, shape)
}
