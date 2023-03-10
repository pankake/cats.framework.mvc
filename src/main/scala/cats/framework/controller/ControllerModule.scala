package cats.framework.controller

import cats.effect.*

import scala.concurrent.duration.*
import cats.effect.cps.*

import scala.concurrent.duration.*
import java.util.concurrent.{Executors, TimeUnit}
import cats.Monad
import cats.effect.kernel.Async
import cats.syntax.all.*

import java.io.*
import java.io.File
import cats.effect.*
import cats.effect.std.{Console, Random}
import cats.framework.controller.readwritefiles.{CatsBracket, CatsResource}
import scalafx.application.Platform
import cats.framework.model.ModelModule
import cats.framework.view.{View, ViewModule}
import scalafx.beans.property.ObjectProperty
import scalafx.scene.Scene

object ControllerModule:

  sealed trait Component extends ReadKeyboard
    with CatsResource with CatsBracket

  trait Interface extends Component with ModelModule.Interface with ViewModule.Interface:

    //stato per la view da renderizzare
    val gs: ObjectProperty[GS]
    //aggiornamento dello stato
    def updateState(gs: ObjectProperty[GS], ns: Any): IO[Unit] = IO(gs.update(gs.value.newState(ns)))
    //creazione della view
    def createView(): Unit = createView(gs)
    //creazione della scena
    def createScene(): Scene = createScene(gs)

    //istanze per la gestione degli effetti
    val loopSequence = Effects.loopSeq
    val loopParallel = Effects.loopPar
    val effectsSequence = Effects.effectsSeq
    val effectsParallel = Effects.effectsPar
    val effectsAsync = Effects.effectsAsync
    val effectsAsyncPar = Effects.effectsAsyncPar
    val effectsSynchDef = Effects.effectsSynchDef
    val effectsSynchSem = Effects.effectsSynchSem
    val effectsSynchCDLatch = Effects.effectsSynchCDLatch
    val effectsExpiration = Effects.effectsExpiration
    val fiberGuarantee = Effects.fiberWithGuarantee
    val fiberUncancellaBLe = Effects.fiberUncancellable
    val fiberBlocking = Effects.fiberBlocking
    val fiberRetry = Effects.fiberRetry
    val fiberRace = Effects.fiberRace


