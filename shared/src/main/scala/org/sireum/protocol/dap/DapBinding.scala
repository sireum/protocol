// #Sireum
/*
 Copyright (c) 2017-2025, Robby, Kansas State University
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.sireum.protocol.dap
import org.sireum._
import org.sireum.parser.json.AST

// Auto-generated from debugAdapterProtocol-v1.68.0.json

/* Debug Adapter Protocol */
/* The Debug Adapter Protocol defines the protocol used between an editor or IDE and a debugger or runtime. */
object DapBinding {

  @datatype trait `.Node` {
    @pure def toAST: AST
  }
  object `.Node` {
    @datatype class Null extends `.Node` {
      @strictpure def toAST: AST = AST.Null(None())
    }
    @datatype class Raw(value: parser.json.AST) extends `.Node` {
      @strictpure def toAST: AST = value
    }
  }

  /* Base Protocol */
  /* Base class of requests, responses, and events. */
  @datatype trait ProtocolMessage extends `.Node` {

    def seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */

    def `type`: String
      /*
        Message type.
        Has to be one of {
          request,
          response,
          event
        }
       */

  }

  /* A client or debug adapter initiated request. */
  @datatype trait Request extends ProtocolMessage {

    def `type`: String
      /*
        Has to be one of {
          request
        }
       */

    def command: String /* The command to execute. */

    def arguments: `.Node`
      /*
        Object containing arguments for the command.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */

  }

  /* A debug adapter initiated event. */
  @datatype trait Event extends ProtocolMessage {

    def `type`: String
      /*
        Has to be one of {
          event
        }
       */

    def event: String /* Type of event. */

    def body: `.Node`
      /*
        Event-specific information.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */

  }

  /* Response for a request. */
  @datatype trait Response extends ProtocolMessage {

    def `type`: String
      /*
        Has to be one of {
          response
        }
       */

    def request_seq: Z /* Sequence number of the corresponding request. */

    def success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */

    def command: String /* The command requested. */

    def message: String
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */

    def body: `.Node`
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */

  }

  @datatype class ErrorResponseBody(
    val errorOpt: Option[Message] /* A structured error message. */
  ) extends `.Node` {
    @strictpure def error: Message /* A structured error message. */ = errorOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (errorOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("error", None()), error.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toErrorResponseBody(ast: AST.Obj): ErrorResponseBody = {
    val map = ast.asMap
    val errorOpt = map.getObjOpt("error").map((o: AST.Obj) => toMessage(o))
    return ErrorResponseBody(errorOpt)
  }

  @pure def mkErrorResponseBody(
  ): ErrorResponseBody = {
    return ErrorResponseBody(None())
  }

  /* On error (whenever `success` is false), the body can provide more details. */
  @datatype class ErrorResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: ErrorResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toErrorResponse(ast: AST.Obj): ErrorResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toErrorResponseBody(map.getObj("body"))
    return ErrorResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkErrorResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: ErrorResponseBody
  ): ErrorResponse = {
    return ErrorResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    The `cancel` request is used by the client in two situations:
    - to indicate that it is no longer interested in the result produced by a specific request issued earlier
    - to cancel a progress sequence.
    Clients should only call this request if the corresponding capability `supportsCancelRequest` is true.
    This request has a hint characteristic: a debug adapter can only be expected to make a 'best effort' in honoring this request but there are no guarantees.
    The `cancel` request may return an error if it could not cancel an operation but a client should refrain from presenting this error to end users.
    The request that got cancelled still needs to send a response back. This can either be a normal result (`success` attribute true) or an error response (`success` attribute false and the `message` set to `cancelled`).
    Returning partial results from a cancelled request is possible but please note that a client has no generic way for detecting that a response is partial or not.
    The progress that got cancelled still needs to send a `progressEnd` event back.
     A client should not assume that progress just got cancelled after sending the `cancel` request.
   */
  @datatype class CancelRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          cancel
        }
       */,
    val argumentsOpt: Option[CancelArguments]
  ) extends Request {
    @strictpure def arguments: CancelArguments = argumentsOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "cancel")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      if (argumentsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toCancelRequest(ast: AST.Obj): CancelRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "cancel")
    val argumentsOpt = map.getObjOpt("arguments").map((o: AST.Obj) => toCancelArguments(o))
    return CancelRequest(seq, `type`, command, argumentsOpt)
  }

  @pure def mkCancelRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */
  ): CancelRequest = {
    return CancelRequest(seq, "request", "cancel", None())
  }

  /* Arguments for `cancel` request. */
  @datatype class CancelArguments(
    val requestIdOpt: Option[Z]
      /*
        The ID (attribute `seq`) of the request to cancel. If missing no request is cancelled.
        Both a `requestId` and a `progressId` can be specified in one request.
       */,
    val progressIdOpt: Option[String]
      /*
        The ID (attribute `progressId`) of the progress to cancel. If missing no progress is cancelled.
        Both a `requestId` and a `progressId` can be specified in one request.
       */
  ) extends `.Node` {
    @strictpure def requestId: Z = requestIdOpt.get
    @strictpure def progressId: String = progressIdOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (requestIdOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("requestId", None()), AST.Int(requestId, None()))
      }
      if (progressIdOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("progressId", None()), AST.Str(progressId, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toCancelArguments(ast: AST.Obj): CancelArguments = {
    val map = ast.asMap
    val requestIdOpt = map.getIntValueOpt("requestId")
    val progressIdOpt = map.getStrValueOpt("progressId")
    return CancelArguments(requestIdOpt, progressIdOpt)
  }

  @pure def mkCancelArguments(
  ): CancelArguments = {
    return CancelArguments(None(), None())
  }

  /* Response to `cancel` request. This is just an acknowledgement, so no body field is required. */
  @datatype class CancelResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toCancelResponse(ast: AST.Obj): CancelResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return CancelResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkCancelResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): CancelResponse = {
    return CancelResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    This event indicates that the debug adapter is ready to accept configuration requests (e.g. `setBreakpoints`, `setExceptionBreakpoints`).
    A debug adapter is expected to send this event when it is ready to accept configuration requests (but not before the `initialize` request has finished).
    The sequence of events/requests is as follows:
    - adapters sends `initialized` event (after the `initialize` request has returned)
    - client sends zero or more `setBreakpoints` requests
    - client sends one `setFunctionBreakpoints` request (if corresponding capability `supportsFunctionBreakpoints` is true)
    - client sends a `setExceptionBreakpoints` request if one or more `exceptionBreakpointFilters` have been defined (or if `supportsConfigurationDoneRequest` is not true)
    - client sends other future configuration requests
    - client sends one `configurationDone` request to indicate the end of the configuration.
   */
  @datatype class InitializedEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          initialized
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Event-specific information.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Event {
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "initialized")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toInitializedEvent(ast: AST.Obj): InitializedEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "initialized")
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return InitializedEvent(seq, `type`, event, bodyOpt)
  }

  @pure def mkInitializedEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */
  ): InitializedEvent = {
    return InitializedEvent(seq, "event", "initialized", None())
  }

  @pure def fromISZZ(seq: ISZ[Z]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ AST.Int(v, None())
    }
    return AST.Arr(elements, None())
  }

  @datatype class StoppedEventBody(
    val reason: String
      /*
        The reason for the event.
        For backward compatibility this string is shown in the UI if the `description` attribute is missing (but it must not be translated).
        Has to be one of {
          step,
          breakpoint,
          exception,
          pause,
          entry,
          goto,
          function breakpoint,
          data breakpoint,
          instruction breakpoint
        }
       */,
    val descriptionOpt: Option[String] /* The full reason for the event, e.g. 'Paused on exception'. This string is shown in the UI as is and can be translated. */,
    val threadIdOpt: Option[Z] /* The thread which was stopped. */,
    val preserveFocusHintOpt: Option[B] /* A value of true hints to the client that this event should not change the focus. */,
    val textOpt: Option[String] /* Additional information. E.g. if reason is `exception`, text contains the exception name. This string is shown in the UI. */,
    val allThreadsStoppedOpt: Option[B]
      /*
        If `allThreadsStopped` is true, a debug adapter can announce that all threads have stopped.
        - The client should use this information to enable that all threads can be expanded to access their stacktraces.
        - If the attribute is missing or false, only the thread with the given `threadId` can be expanded.
       */,
    val hitBreakpointIdsOpt: Option[ISZ[Z]]
      /*
        Ids of the breakpoints that triggered the event. In most cases there is only a single breakpoint but here are some examples for multiple breakpoints:
        - Different types of breakpoints map to the same location.
        - Multiple source breakpoints get collapsed to the same instruction by the compiler/runtime.
        - Multiple function breakpoints with different function names map to the same location.
       */
  ) extends `.Node` {
    @strictpure def description: String /* The full reason for the event, e.g. 'Paused on exception'. This string is shown in the UI as is and can be translated. */ = descriptionOpt.get
    @strictpure def threadId: Z /* The thread which was stopped. */ = threadIdOpt.get
    @strictpure def preserveFocusHint: B /* A value of true hints to the client that this event should not change the focus. */ = preserveFocusHintOpt.get
    @strictpure def text: String /* Additional information. E.g. if reason is `exception`, text contains the exception name. This string is shown in the UI. */ = textOpt.get
    @strictpure def allThreadsStopped: B
      /*
        If `allThreadsStopped` is true, a debug adapter can announce that all threads have stopped.
        - The client should use this information to enable that all threads can be expanded to access their stacktraces.
        - If the attribute is missing or false, only the thread with the given `threadId` can be expanded.
       */ = allThreadsStoppedOpt.get
    @strictpure def hitBreakpointIds: ISZ[Z]
      /*
        Ids of the breakpoints that triggered the event. In most cases there is only a single breakpoint but here are some examples for multiple breakpoints:
        - Different types of breakpoints map to the same location.
        - Multiple source breakpoints get collapsed to the same instruction by the compiler/runtime.
        - Multiple function breakpoints with different function names map to the same location.
       */ = hitBreakpointIdsOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      assert(reason == "step" || reason == "breakpoint" || reason == "exception" || reason == "pause" || reason == "entry" || reason == "goto" || reason == "function breakpoint" || reason == "data breakpoint" || reason == "instruction breakpoint")
      kvs = kvs :+ AST.KeyValue(AST.Str("reason", None()), AST.Str(reason, None()))
      if (descriptionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("description", None()), AST.Str(description, None()))
      }
      if (threadIdOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("threadId", None()), AST.Int(threadId, None()))
      }
      if (preserveFocusHintOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("preserveFocusHint", None()), AST.Bool(preserveFocusHint, None()))
      }
      if (textOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("text", None()), AST.Str(text, None()))
      }
      if (allThreadsStoppedOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("allThreadsStopped", None()), AST.Bool(allThreadsStopped, None()))
      }
      if (hitBreakpointIdsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("hitBreakpointIds", None()), fromISZZ(hitBreakpointIds))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZZ(ast: AST.Arr): ISZ[Z] = {
    var r = ISZ[Z]()
    for (v <- ast.values) {
      r = r :+ v.asInstanceOf[AST.Int].value
    }
    return r
  }

  @pure def toStoppedEventBody(ast: AST.Obj): StoppedEventBody = {
    val map = ast.asMap
    val reason = map.getStr("reason").value
    assert(reason == "step" || reason == "breakpoint" || reason == "exception" || reason == "pause" || reason == "entry" || reason == "goto" || reason == "function breakpoint" || reason == "data breakpoint" || reason == "instruction breakpoint")
    val descriptionOpt = map.getStrValueOpt("description")
    val threadIdOpt = map.getIntValueOpt("threadId")
    val preserveFocusHintOpt = map.getBoolValueOpt("preserveFocusHint")
    val textOpt = map.getStrValueOpt("text")
    val allThreadsStoppedOpt = map.getBoolValueOpt("allThreadsStopped")
    val hitBreakpointIdsOpt = map.getArrOpt("hitBreakpointIds").map((o: AST.Arr) => toISZZ(o))
    return StoppedEventBody(reason, descriptionOpt, threadIdOpt, preserveFocusHintOpt, textOpt, allThreadsStoppedOpt, hitBreakpointIdsOpt)
  }

  @pure def mkStoppedEventBody(
    reason: String
      /*
        The reason for the event.
        For backward compatibility this string is shown in the UI if the `description` attribute is missing (but it must not be translated).
        Has to be one of {
          step,
          breakpoint,
          exception,
          pause,
          entry,
          goto,
          function breakpoint,
          data breakpoint,
          instruction breakpoint
        }
       */
  ): StoppedEventBody = {
    return StoppedEventBody(reason, None(), None(), None(), None(), None(), None())
  }

  /*
    The event indicates that the execution of the debuggee has stopped due to some condition.
    This can be caused by a breakpoint previously set, a stepping request has completed, by executing a debugger statement etc.
   */
  @datatype class StoppedEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          stopped
        }
       */,
    val body: StoppedEventBody
  ) extends Event {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "stopped")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStoppedEvent(ast: AST.Obj): StoppedEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "stopped")
    val body = toStoppedEventBody(map.getObj("body"))
    return StoppedEvent(seq, `type`, event, body)
  }

  @pure def mkStoppedEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    body: StoppedEventBody
  ): StoppedEvent = {
    return StoppedEvent(seq, "event", "stopped", body)
  }

  @datatype class ContinuedEventBody(
    val threadId: Z /* The thread which was continued. */,
    val allThreadsContinuedOpt: Option[B] /* If `allThreadsContinued` is true, a debug adapter can announce that all threads have continued. */
  ) extends `.Node` {
    @strictpure def allThreadsContinued: B /* If `allThreadsContinued` is true, a debug adapter can announce that all threads have continued. */ = allThreadsContinuedOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("threadId", None()), AST.Int(threadId, None()))
      if (allThreadsContinuedOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("allThreadsContinued", None()), AST.Bool(allThreadsContinued, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toContinuedEventBody(ast: AST.Obj): ContinuedEventBody = {
    val map = ast.asMap
    val threadId = map.getInt("threadId").value
    val allThreadsContinuedOpt = map.getBoolValueOpt("allThreadsContinued")
    return ContinuedEventBody(threadId, allThreadsContinuedOpt)
  }

  @pure def mkContinuedEventBody(
    threadId: Z /* The thread which was continued. */
  ): ContinuedEventBody = {
    return ContinuedEventBody(threadId, None())
  }

  /*
    The event indicates that the execution of the debuggee has continued.
    Please note: a debug adapter is not expected to send this event in response to a request that implies that execution continues, e.g. `launch` or `continue`.
    It is only necessary to send a `continued` event if there was no previous request that implied this.
   */
  @datatype class ContinuedEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          continued
        }
       */,
    val body: ContinuedEventBody
  ) extends Event {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "continued")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toContinuedEvent(ast: AST.Obj): ContinuedEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "continued")
    val body = toContinuedEventBody(map.getObj("body"))
    return ContinuedEvent(seq, `type`, event, body)
  }

  @pure def mkContinuedEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    body: ContinuedEventBody
  ): ContinuedEvent = {
    return ContinuedEvent(seq, "event", "continued", body)
  }

  @datatype class ExitedEventBody(
    val exitCode: Z /* The exit code returned from the debuggee. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("exitCode", None()), AST.Int(exitCode, None()))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toExitedEventBody(ast: AST.Obj): ExitedEventBody = {
    val map = ast.asMap
    val exitCode = map.getInt("exitCode").value
    return ExitedEventBody(exitCode)
  }

  @pure def mkExitedEventBody(
    exitCode: Z /* The exit code returned from the debuggee. */
  ): ExitedEventBody = {
    return ExitedEventBody(exitCode)
  }

  /* The event indicates that the debuggee has exited and returns its exit code. */
  @datatype class ExitedEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          exited
        }
       */,
    val body: ExitedEventBody
  ) extends Event {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "exited")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toExitedEvent(ast: AST.Obj): ExitedEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "exited")
    val body = toExitedEventBody(map.getObj("body"))
    return ExitedEvent(seq, `type`, event, body)
  }

  @pure def mkExitedEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    body: ExitedEventBody
  ): ExitedEvent = {
    return ExitedEvent(seq, "event", "exited", body)
  }

  @datatype class TerminatedEventBody(
    val restartOpt: Option[`.Node`]
      /*
        A debug adapter may set `restart` to true (or to an arbitrary object) to request that the client restarts the session.
        The value is not interpreted by the client and passed unmodified as an attribute `__restart` to the `launch` and `attach` requests.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends `.Node` {
    @strictpure def restart: `.Node`
      /*
        A debug adapter may set `restart` to true (or to an arbitrary object) to request that the client restarts the session.
        The value is not interpreted by the client and passed unmodified as an attribute `__restart` to the `launch` and `attach` requests.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */ = restartOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (restartOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("restart", None()), restart.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toTerminatedEventBody(ast: AST.Obj): TerminatedEventBody = {
    val map = ast.asMap
    val restartOpt = map.getOpt("restart").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return TerminatedEventBody(restartOpt)
  }

  @pure def mkTerminatedEventBody(
  ): TerminatedEventBody = {
    return TerminatedEventBody(None())
  }

  /* The event indicates that debugging of the debuggee has terminated. This does **not** mean that the debuggee itself has exited. */
  @datatype class TerminatedEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          terminated
        }
       */,
    val bodyOpt: Option[TerminatedEventBody]
  ) extends Event {
    @strictpure def body: TerminatedEventBody = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "terminated")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toTerminatedEvent(ast: AST.Obj): TerminatedEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "terminated")
    val bodyOpt = map.getObjOpt("body").map((o: AST.Obj) => toTerminatedEventBody(o))
    return TerminatedEvent(seq, `type`, event, bodyOpt)
  }

  @pure def mkTerminatedEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */
  ): TerminatedEvent = {
    return TerminatedEvent(seq, "event", "terminated", None())
  }

  @datatype class ThreadEventBody(
    val reason: String
      /*
        The reason for the event.
        Has to be one of {
          started,
          exited
        }
       */,
    val threadId: Z /* The identifier of the thread. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      assert(reason == "started" || reason == "exited")
      kvs = kvs :+ AST.KeyValue(AST.Str("reason", None()), AST.Str(reason, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("threadId", None()), AST.Int(threadId, None()))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toThreadEventBody(ast: AST.Obj): ThreadEventBody = {
    val map = ast.asMap
    val reason = map.getStr("reason").value
    assert(reason == "started" || reason == "exited")
    val threadId = map.getInt("threadId").value
    return ThreadEventBody(reason, threadId)
  }

  @pure def mkThreadEventBody(
    reason: String
      /*
        The reason for the event.
        Has to be one of {
          started,
          exited
        }
       */,
    threadId: Z /* The identifier of the thread. */
  ): ThreadEventBody = {
    return ThreadEventBody(reason, threadId)
  }

  /* The event indicates that a thread has started or exited. */
  @datatype class ThreadEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          thread
        }
       */,
    val body: ThreadEventBody
  ) extends Event {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "thread")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toThreadEvent(ast: AST.Obj): ThreadEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "thread")
    val body = toThreadEventBody(map.getObj("body"))
    return ThreadEvent(seq, `type`, event, body)
  }

  @pure def mkThreadEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    body: ThreadEventBody
  ): ThreadEvent = {
    return ThreadEvent(seq, "event", "thread", body)
  }

  @datatype class OutputEventBody(
    val categoryOpt: Option[String]
      /*
        The output category. If not specified or if the category is not understood by the client, `console` is assumed.
        Has to be one of {
          console /* Show the output in the client's default message UI, e.g. a 'debug console'. This category should only be used for informational output from the debugger (as opposed to the debuggee). */,
          important /* A hint for the client to show the output in the client's UI for important and highly visible information, e.g. as a popup notification. This category should only be used for important messages from the debugger (as opposed to the debuggee). Since this category value is a hint, clients might ignore the hint and assume the `console` category. */,
          stdout /* Show the output as normal program output from the debuggee. */,
          stderr /* Show the output as error program output from the debuggee. */,
          telemetry /* Send the output to telemetry instead of showing it to the user. */
        }
       */,
    val output: String /* The output to report. */,
    val groupOpt: Option[String]
      /*
        Support for keeping an output log organized by grouping related messages.
        Has to be one of {
          start /*
            Start a new group in expanded mode. Subsequent output events are members of the group and should be shown indented.
            The `output` attribute becomes the name of the group and is not indented.
           */,
          startCollapsed /*
            Start a new group in collapsed mode. Subsequent output events are members of the group and should be shown indented (as soon as the group is expanded).
            The `output` attribute becomes the name of the group and is not indented.
           */,
          end /*
            End the current group and decrease the indentation of subsequent output events.
            A non-empty `output` attribute is shown as the unindented end of the group.
           */
        }
       */,
    val variablesReferenceOpt: Option[Z] /* If an attribute `variablesReference` exists and its value is > 0, the output contains objects which can be retrieved by passing `variablesReference` to the `variables` request as long as execution remains suspended. See 'Lifetime of Object References' in the Overview section for details. */,
    val sourceOpt: Option[Source] /* The source location where the output was produced. */,
    val lineOpt: Option[Z] /* The source location's line where the output was produced. */,
    val columnOpt: Option[Z] /* The position in `line` where the output was produced. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. */,
    val dataOpt: Option[`.Node`]
      /*
        Additional data to report. For the `telemetry` category the data is sent to telemetry, for the other categories the data is shown in JSON format.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */,
    val locationReferenceOpt: Option[Z]
      /*
        A reference that allows the client to request the location where the new value is declared. For example, if the logged value is function pointer, the adapter may be able to look up the function's location. This should be present only if the adapter is likely to be able to resolve the location.
        This reference shares the same lifetime as the `variablesReference`. See 'Lifetime of Object References' in the Overview section for details.
       */
  ) extends `.Node` {
    @strictpure def category: String
      /*
        The output category. If not specified or if the category is not understood by the client, `console` is assumed.
        Has to be one of {
          console /* Show the output in the client's default message UI, e.g. a 'debug console'. This category should only be used for informational output from the debugger (as opposed to the debuggee). */,
          important /* A hint for the client to show the output in the client's UI for important and highly visible information, e.g. as a popup notification. This category should only be used for important messages from the debugger (as opposed to the debuggee). Since this category value is a hint, clients might ignore the hint and assume the `console` category. */,
          stdout /* Show the output as normal program output from the debuggee. */,
          stderr /* Show the output as error program output from the debuggee. */,
          telemetry /* Send the output to telemetry instead of showing it to the user. */
        }
       */ = categoryOpt.get
    @strictpure def group: String
      /*
        Support for keeping an output log organized by grouping related messages.
        Has to be one of {
          start /*
            Start a new group in expanded mode. Subsequent output events are members of the group and should be shown indented.
            The `output` attribute becomes the name of the group and is not indented.
           */,
          startCollapsed /*
            Start a new group in collapsed mode. Subsequent output events are members of the group and should be shown indented (as soon as the group is expanded).
            The `output` attribute becomes the name of the group and is not indented.
           */,
          end /*
            End the current group and decrease the indentation of subsequent output events.
            A non-empty `output` attribute is shown as the unindented end of the group.
           */
        }
       */ = groupOpt.get
    @strictpure def variablesReference: Z /* If an attribute `variablesReference` exists and its value is > 0, the output contains objects which can be retrieved by passing `variablesReference` to the `variables` request as long as execution remains suspended. See 'Lifetime of Object References' in the Overview section for details. */ = variablesReferenceOpt.get
    @strictpure def source: Source /* The source location where the output was produced. */ = sourceOpt.get
    @strictpure def line: Z /* The source location's line where the output was produced. */ = lineOpt.get
    @strictpure def column: Z /* The position in `line` where the output was produced. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. */ = columnOpt.get
    @strictpure def data: `.Node`
      /*
        Additional data to report. For the `telemetry` category the data is sent to telemetry, for the other categories the data is shown in JSON format.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */ = dataOpt.get
    @strictpure def locationReference: Z
      /*
        A reference that allows the client to request the location where the new value is declared. For example, if the logged value is function pointer, the adapter may be able to look up the function's location. This should be present only if the adapter is likely to be able to resolve the location.
        This reference shares the same lifetime as the `variablesReference`. See 'Lifetime of Object References' in the Overview section for details.
       */ = locationReferenceOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      assert(category == "console" || category == "important" || category == "stdout" || category == "stderr" || category == "telemetry")
      if (categoryOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("category", None()), AST.Str(category, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("output", None()), AST.Str(output, None()))
      assert(group == "start" || group == "startCollapsed" || group == "end")
      if (groupOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("group", None()), AST.Str(group, None()))
      }
      if (variablesReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("variablesReference", None()), AST.Int(variablesReference, None()))
      }
      if (sourceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("source", None()), source.toAST)
      }
      if (lineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("line", None()), AST.Int(line, None()))
      }
      if (columnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("column", None()), AST.Int(column, None()))
      }
      if (dataOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("data", None()), data.toAST)
      }
      if (locationReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("locationReference", None()), AST.Int(locationReference, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toOutputEventBody(ast: AST.Obj): OutputEventBody = {
    val map = ast.asMap
    val categoryOpt = map.getStrValueOpt("category")
    categoryOpt match {
      case Some(s) => assert(s == "console" || s == "important" || s == "stdout" || s == "stderr" || s == "telemetry")
      case _ =>
    }
    val output = map.getStr("output").value
    val groupOpt = map.getStrValueOpt("group")
    groupOpt match {
      case Some(s) => assert(s == "start" || s == "startCollapsed" || s == "end")
      case _ =>
    }
    val variablesReferenceOpt = map.getIntValueOpt("variablesReference")
    val sourceOpt = map.getObjOpt("source").map((o: AST.Obj) => toSource(o))
    val lineOpt = map.getIntValueOpt("line")
    val columnOpt = map.getIntValueOpt("column")
    val dataOpt = map.getOpt("data").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    val locationReferenceOpt = map.getIntValueOpt("locationReference")
    return OutputEventBody(categoryOpt, output, groupOpt, variablesReferenceOpt, sourceOpt, lineOpt, columnOpt, dataOpt, locationReferenceOpt)
  }

  @pure def mkOutputEventBody(
    output: String /* The output to report. */
  ): OutputEventBody = {
    return OutputEventBody(None(), output, None(), None(), None(), None(), None(), None(), None())
  }

  /* The event indicates that the target has produced some output. */
  @datatype class OutputEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          output
        }
       */,
    val body: OutputEventBody
  ) extends Event {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "output")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toOutputEvent(ast: AST.Obj): OutputEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "output")
    val body = toOutputEventBody(map.getObj("body"))
    return OutputEvent(seq, `type`, event, body)
  }

  @pure def mkOutputEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    body: OutputEventBody
  ): OutputEvent = {
    return OutputEvent(seq, "event", "output", body)
  }

  @datatype class BreakpointEventBody(
    val reason: String
      /*
        The reason for the event.
        Has to be one of {
          changed,
          new,
          removed
        }
       */,
    val breakpoint: Breakpoint /* The `id` attribute is used to find the target breakpoint, the other attributes are used as the new values. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      assert(reason == "changed" || reason == "new" || reason == "removed")
      kvs = kvs :+ AST.KeyValue(AST.Str("reason", None()), AST.Str(reason, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("breakpoint", None()), breakpoint.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toBreakpointEventBody(ast: AST.Obj): BreakpointEventBody = {
    val map = ast.asMap
    val reason = map.getStr("reason").value
    assert(reason == "changed" || reason == "new" || reason == "removed")
    val breakpoint = toBreakpoint(map.getObj("breakpoint"))
    return BreakpointEventBody(reason, breakpoint)
  }

  @pure def mkBreakpointEventBody(
    reason: String
      /*
        The reason for the event.
        Has to be one of {
          changed,
          new,
          removed
        }
       */,
    breakpoint: Breakpoint /* The `id` attribute is used to find the target breakpoint, the other attributes are used as the new values. */
  ): BreakpointEventBody = {
    return BreakpointEventBody(reason, breakpoint)
  }

  /* The event indicates that some information about a breakpoint has changed. */
  @datatype class BreakpointEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          breakpoint
        }
       */,
    val body: BreakpointEventBody
  ) extends Event {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "breakpoint")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toBreakpointEvent(ast: AST.Obj): BreakpointEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "breakpoint")
    val body = toBreakpointEventBody(map.getObj("body"))
    return BreakpointEvent(seq, `type`, event, body)
  }

  @pure def mkBreakpointEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    body: BreakpointEventBody
  ): BreakpointEvent = {
    return BreakpointEvent(seq, "event", "breakpoint", body)
  }

  @datatype class ModuleEventBody(
    val reason: String
      /*
        The reason for the event.
        Has to be one of {
          new,
          changed,
          removed
        }
       */,
    val module: Module /* The new, changed, or removed module. In case of `removed` only the module id is used. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      assert(reason == "new" || reason == "changed" || reason == "removed")
      kvs = kvs :+ AST.KeyValue(AST.Str("reason", None()), AST.Str(reason, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("module", None()), module.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toModuleEventBody(ast: AST.Obj): ModuleEventBody = {
    val map = ast.asMap
    val reason = map.getStr("reason").value
    assert(reason == "new" || reason == "changed" || reason == "removed")
    val module = toModule(map.getObj("module"))
    return ModuleEventBody(reason, module)
  }

  @pure def mkModuleEventBody(
    reason: String
      /*
        The reason for the event.
        Has to be one of {
          new,
          changed,
          removed
        }
       */,
    module: Module /* The new, changed, or removed module. In case of `removed` only the module id is used. */
  ): ModuleEventBody = {
    return ModuleEventBody(reason, module)
  }

  /* The event indicates that some information about a module has changed. */
  @datatype class ModuleEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          module
        }
       */,
    val body: ModuleEventBody
  ) extends Event {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "module")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toModuleEvent(ast: AST.Obj): ModuleEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "module")
    val body = toModuleEventBody(map.getObj("body"))
    return ModuleEvent(seq, `type`, event, body)
  }

  @pure def mkModuleEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    body: ModuleEventBody
  ): ModuleEvent = {
    return ModuleEvent(seq, "event", "module", body)
  }

  @datatype class LoadedSourceEventBody(
    val reason: String
      /*
        The reason for the event.
        Has to be one of {
          new,
          changed,
          removed
        }
       */,
    val source: Source /* The new, changed, or removed source. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      assert(reason == "new" || reason == "changed" || reason == "removed")
      kvs = kvs :+ AST.KeyValue(AST.Str("reason", None()), AST.Str(reason, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("source", None()), source.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toLoadedSourceEventBody(ast: AST.Obj): LoadedSourceEventBody = {
    val map = ast.asMap
    val reason = map.getStr("reason").value
    assert(reason == "new" || reason == "changed" || reason == "removed")
    val source = toSource(map.getObj("source"))
    return LoadedSourceEventBody(reason, source)
  }

  @pure def mkLoadedSourceEventBody(
    reason: String
      /*
        The reason for the event.
        Has to be one of {
          new,
          changed,
          removed
        }
       */,
    source: Source /* The new, changed, or removed source. */
  ): LoadedSourceEventBody = {
    return LoadedSourceEventBody(reason, source)
  }

  /* The event indicates that some source has been added, changed, or removed from the set of all loaded sources. */
  @datatype class LoadedSourceEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          loadedSource
        }
       */,
    val body: LoadedSourceEventBody
  ) extends Event {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "loadedSource")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toLoadedSourceEvent(ast: AST.Obj): LoadedSourceEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "loadedSource")
    val body = toLoadedSourceEventBody(map.getObj("body"))
    return LoadedSourceEvent(seq, `type`, event, body)
  }

  @pure def mkLoadedSourceEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    body: LoadedSourceEventBody
  ): LoadedSourceEvent = {
    return LoadedSourceEvent(seq, "event", "loadedSource", body)
  }

  @datatype class ProcessEventBody(
    val name: String /* The logical name of the process. This is usually the full path to process's executable file. Example: /home/example/myproj/program.js. */,
    val systemProcessIdOpt: Option[Z] /* The process ID of the debugged process, as assigned by the operating system. This property should be omitted for logical processes that do not map to operating system processes on the machine. */,
    val isLocalProcessOpt: Option[B] /* If true, the process is running on the same computer as the debug adapter. */,
    val startMethodOpt: Option[String]
      /*
        Describes how the debug engine started debugging this process.
        Has to be one of {
          launch /* Process was launched under the debugger. */,
          attach /* Debugger attached to an existing process. */,
          attachForSuspendedLaunch /* A project launcher component has launched a new process in a suspended state and then asked the debugger to attach. */
        }
       */,
    val pointerSizeOpt: Option[Z] /* The size of a pointer or address for this process, in bits. This value may be used by clients when formatting addresses for display. */
  ) extends `.Node` {
    @strictpure def systemProcessId: Z /* The process ID of the debugged process, as assigned by the operating system. This property should be omitted for logical processes that do not map to operating system processes on the machine. */ = systemProcessIdOpt.get
    @strictpure def isLocalProcess: B /* If true, the process is running on the same computer as the debug adapter. */ = isLocalProcessOpt.get
    @strictpure def startMethod: String
      /*
        Describes how the debug engine started debugging this process.
        Has to be one of {
          launch /* Process was launched under the debugger. */,
          attach /* Debugger attached to an existing process. */,
          attachForSuspendedLaunch /* A project launcher component has launched a new process in a suspended state and then asked the debugger to attach. */
        }
       */ = startMethodOpt.get
    @strictpure def pointerSize: Z /* The size of a pointer or address for this process, in bits. This value may be used by clients when formatting addresses for display. */ = pointerSizeOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("name", None()), AST.Str(name, None()))
      if (systemProcessIdOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("systemProcessId", None()), AST.Int(systemProcessId, None()))
      }
      if (isLocalProcessOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("isLocalProcess", None()), AST.Bool(isLocalProcess, None()))
      }
      assert(startMethod == "launch" || startMethod == "attach" || startMethod == "attachForSuspendedLaunch")
      if (startMethodOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("startMethod", None()), AST.Str(startMethod, None()))
      }
      if (pointerSizeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("pointerSize", None()), AST.Int(pointerSize, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toProcessEventBody(ast: AST.Obj): ProcessEventBody = {
    val map = ast.asMap
    val name = map.getStr("name").value
    val systemProcessIdOpt = map.getIntValueOpt("systemProcessId")
    val isLocalProcessOpt = map.getBoolValueOpt("isLocalProcess")
    val startMethodOpt = map.getStrValueOpt("startMethod")
    startMethodOpt match {
      case Some(s) => assert(s == "launch" || s == "attach" || s == "attachForSuspendedLaunch")
      case _ =>
    }
    val pointerSizeOpt = map.getIntValueOpt("pointerSize")
    return ProcessEventBody(name, systemProcessIdOpt, isLocalProcessOpt, startMethodOpt, pointerSizeOpt)
  }

  @pure def mkProcessEventBody(
    name: String /* The logical name of the process. This is usually the full path to process's executable file. Example: /home/example/myproj/program.js. */
  ): ProcessEventBody = {
    return ProcessEventBody(name, None(), None(), None(), None())
  }

  /* The event indicates that the debugger has begun debugging a new process. Either one that it has launched, or one that it has attached to. */
  @datatype class ProcessEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          process
        }
       */,
    val body: ProcessEventBody
  ) extends Event {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "process")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toProcessEvent(ast: AST.Obj): ProcessEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "process")
    val body = toProcessEventBody(map.getObj("body"))
    return ProcessEvent(seq, `type`, event, body)
  }

  @pure def mkProcessEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    body: ProcessEventBody
  ): ProcessEvent = {
    return ProcessEvent(seq, "event", "process", body)
  }

  @datatype class CapabilitiesEventBody(
    val capabilities: Capabilities /* The set of updated capabilities. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("capabilities", None()), capabilities.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toCapabilitiesEventBody(ast: AST.Obj): CapabilitiesEventBody = {
    val map = ast.asMap
    val capabilities = toCapabilities(map.getObj("capabilities"))
    return CapabilitiesEventBody(capabilities)
  }

  @pure def mkCapabilitiesEventBody(
    capabilities: Capabilities /* The set of updated capabilities. */
  ): CapabilitiesEventBody = {
    return CapabilitiesEventBody(capabilities)
  }

  /*
    The event indicates that one or more capabilities have changed.
    Since the capabilities are dependent on the client and its UI, it might not be possible to change that at random times (or too late).
    Consequently this event has a hint characteristic: a client can only be expected to make a 'best effort' in honoring individual capabilities but there are no guarantees.
    Only changed capabilities need to be included, all other capabilities keep their values.
   */
  @datatype class CapabilitiesEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          capabilities
        }
       */,
    val body: CapabilitiesEventBody
  ) extends Event {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "capabilities")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toCapabilitiesEvent(ast: AST.Obj): CapabilitiesEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "capabilities")
    val body = toCapabilitiesEventBody(map.getObj("body"))
    return CapabilitiesEvent(seq, `type`, event, body)
  }

  @pure def mkCapabilitiesEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    body: CapabilitiesEventBody
  ): CapabilitiesEvent = {
    return CapabilitiesEvent(seq, "event", "capabilities", body)
  }

  @datatype class ProgressStartEventBody(
    val progressId: String
      /*
        An ID that can be used in subsequent `progressUpdate` and `progressEnd` events to make them refer to the same progress reporting.
        IDs must be unique within a debug session.
       */,
    val title: String /* Short title of the progress reporting. Shown in the UI to describe the long running operation. */,
    val requestIdOpt: Option[Z]
      /*
        The request ID that this progress report is related to. If specified a debug adapter is expected to emit progress events for the long running request until the request has been either completed or cancelled.
        If the request ID is omitted, the progress report is assumed to be related to some general activity of the debug adapter.
       */,
    val cancellableOpt: Option[B]
      /*
        If true, the request that reports progress may be cancelled with a `cancel` request.
        So this property basically controls whether the client should use UX that supports cancellation.
        Clients that don't support cancellation are allowed to ignore the setting.
       */,
    val messageOpt: Option[String] /* More detailed progress message. */,
    val percentageOpt: Option[F64] /* Progress percentage to display (value range: 0 to 100). If omitted no percentage is shown. */
  ) extends `.Node` {
    @strictpure def requestId: Z
      /*
        The request ID that this progress report is related to. If specified a debug adapter is expected to emit progress events for the long running request until the request has been either completed or cancelled.
        If the request ID is omitted, the progress report is assumed to be related to some general activity of the debug adapter.
       */ = requestIdOpt.get
    @strictpure def cancellable: B
      /*
        If true, the request that reports progress may be cancelled with a `cancel` request.
        So this property basically controls whether the client should use UX that supports cancellation.
        Clients that don't support cancellation are allowed to ignore the setting.
       */ = cancellableOpt.get
    @strictpure def message: String /* More detailed progress message. */ = messageOpt.get
    @strictpure def percentage: F64 /* Progress percentage to display (value range: 0 to 100). If omitted no percentage is shown. */ = percentageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("progressId", None()), AST.Str(progressId, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("title", None()), AST.Str(title, None()))
      if (requestIdOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("requestId", None()), AST.Int(requestId, None()))
      }
      if (cancellableOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("cancellable", None()), AST.Bool(cancellable, None()))
      }
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (percentageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("percentage", None()), AST.Dbl(percentage, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toProgressStartEventBody(ast: AST.Obj): ProgressStartEventBody = {
    val map = ast.asMap
    val progressId = map.getStr("progressId").value
    val title = map.getStr("title").value
    val requestIdOpt = map.getIntValueOpt("requestId")
    val cancellableOpt = map.getBoolValueOpt("cancellable")
    val messageOpt = map.getStrValueOpt("message")
    val percentageOpt = map.getDblValueOpt("percentage")
    return ProgressStartEventBody(progressId, title, requestIdOpt, cancellableOpt, messageOpt, percentageOpt)
  }

  @pure def mkProgressStartEventBody(
    progressId: String
      /*
        An ID that can be used in subsequent `progressUpdate` and `progressEnd` events to make them refer to the same progress reporting.
        IDs must be unique within a debug session.
       */,
    title: String /* Short title of the progress reporting. Shown in the UI to describe the long running operation. */
  ): ProgressStartEventBody = {
    return ProgressStartEventBody(progressId, title, None(), None(), None(), None())
  }

  /*
    The event signals that a long running operation is about to start and provides additional information for the client to set up a corresponding progress and cancellation UI.
    The client is free to delay the showing of the UI in order to reduce flicker.
    This event should only be sent if the corresponding capability `supportsProgressReporting` is true.
   */
  @datatype class ProgressStartEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          progressStart
        }
       */,
    val body: ProgressStartEventBody
  ) extends Event {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "progressStart")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toProgressStartEvent(ast: AST.Obj): ProgressStartEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "progressStart")
    val body = toProgressStartEventBody(map.getObj("body"))
    return ProgressStartEvent(seq, `type`, event, body)
  }

  @pure def mkProgressStartEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    body: ProgressStartEventBody
  ): ProgressStartEvent = {
    return ProgressStartEvent(seq, "event", "progressStart", body)
  }

  @datatype class ProgressUpdateEventBody(
    val progressId: String /* The ID that was introduced in the initial `progressStart` event. */,
    val messageOpt: Option[String] /* More detailed progress message. If omitted, the previous message (if any) is used. */,
    val percentageOpt: Option[F64] /* Progress percentage to display (value range: 0 to 100). If omitted no percentage is shown. */
  ) extends `.Node` {
    @strictpure def message: String /* More detailed progress message. If omitted, the previous message (if any) is used. */ = messageOpt.get
    @strictpure def percentage: F64 /* Progress percentage to display (value range: 0 to 100). If omitted no percentage is shown. */ = percentageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("progressId", None()), AST.Str(progressId, None()))
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (percentageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("percentage", None()), AST.Dbl(percentage, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toProgressUpdateEventBody(ast: AST.Obj): ProgressUpdateEventBody = {
    val map = ast.asMap
    val progressId = map.getStr("progressId").value
    val messageOpt = map.getStrValueOpt("message")
    val percentageOpt = map.getDblValueOpt("percentage")
    return ProgressUpdateEventBody(progressId, messageOpt, percentageOpt)
  }

  @pure def mkProgressUpdateEventBody(
    progressId: String /* The ID that was introduced in the initial `progressStart` event. */
  ): ProgressUpdateEventBody = {
    return ProgressUpdateEventBody(progressId, None(), None())
  }

  /*
    The event signals that the progress reporting needs to be updated with a new message and/or percentage.
    The client does not have to update the UI immediately, but the clients needs to keep track of the message and/or percentage values.
    This event should only be sent if the corresponding capability `supportsProgressReporting` is true.
   */
  @datatype class ProgressUpdateEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          progressUpdate
        }
       */,
    val body: ProgressUpdateEventBody
  ) extends Event {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "progressUpdate")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toProgressUpdateEvent(ast: AST.Obj): ProgressUpdateEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "progressUpdate")
    val body = toProgressUpdateEventBody(map.getObj("body"))
    return ProgressUpdateEvent(seq, `type`, event, body)
  }

  @pure def mkProgressUpdateEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    body: ProgressUpdateEventBody
  ): ProgressUpdateEvent = {
    return ProgressUpdateEvent(seq, "event", "progressUpdate", body)
  }

  @datatype class ProgressEndEventBody(
    val progressId: String /* The ID that was introduced in the initial `ProgressStartEvent`. */,
    val messageOpt: Option[String] /* More detailed progress message. If omitted, the previous message (if any) is used. */
  ) extends `.Node` {
    @strictpure def message: String /* More detailed progress message. If omitted, the previous message (if any) is used. */ = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("progressId", None()), AST.Str(progressId, None()))
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toProgressEndEventBody(ast: AST.Obj): ProgressEndEventBody = {
    val map = ast.asMap
    val progressId = map.getStr("progressId").value
    val messageOpt = map.getStrValueOpt("message")
    return ProgressEndEventBody(progressId, messageOpt)
  }

  @pure def mkProgressEndEventBody(
    progressId: String /* The ID that was introduced in the initial `ProgressStartEvent`. */
  ): ProgressEndEventBody = {
    return ProgressEndEventBody(progressId, None())
  }

  /*
    The event signals the end of the progress reporting with a final message.
    This event should only be sent if the corresponding capability `supportsProgressReporting` is true.
   */
  @datatype class ProgressEndEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          progressEnd
        }
       */,
    val body: ProgressEndEventBody
  ) extends Event {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "progressEnd")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toProgressEndEvent(ast: AST.Obj): ProgressEndEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "progressEnd")
    val body = toProgressEndEventBody(map.getObj("body"))
    return ProgressEndEvent(seq, `type`, event, body)
  }

  @pure def mkProgressEndEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    body: ProgressEndEventBody
  ): ProgressEndEvent = {
    return ProgressEndEvent(seq, "event", "progressEnd", body)
  }

  @pure def fromISZInvalidatedAreas(seq: ISZ[InvalidatedAreas]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @datatype class InvalidatedEventBody(
    val areasOpt: Option[ISZ[InvalidatedAreas]] /* Set of logical areas that got invalidated. This property has a hint characteristic: a client can only be expected to make a 'best effort' in honoring the areas but there are no guarantees. If this property is missing, empty, or if values are not understood, the client should assume a single value `all`. */,
    val threadIdOpt: Option[Z] /* If specified, the client only needs to refetch data related to this thread. */,
    val stackFrameIdOpt: Option[Z] /* If specified, the client only needs to refetch data related to this stack frame (and the `threadId` is ignored). */
  ) extends `.Node` {
    @strictpure def areas: ISZ[InvalidatedAreas] /* Set of logical areas that got invalidated. This property has a hint characteristic: a client can only be expected to make a 'best effort' in honoring the areas but there are no guarantees. If this property is missing, empty, or if values are not understood, the client should assume a single value `all`. */ = areasOpt.get
    @strictpure def threadId: Z /* If specified, the client only needs to refetch data related to this thread. */ = threadIdOpt.get
    @strictpure def stackFrameId: Z /* If specified, the client only needs to refetch data related to this stack frame (and the `threadId` is ignored). */ = stackFrameIdOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (areasOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("areas", None()), fromISZInvalidatedAreas(areas))
      }
      if (threadIdOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("threadId", None()), AST.Int(threadId, None()))
      }
      if (stackFrameIdOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("stackFrameId", None()), AST.Int(stackFrameId, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZInvalidatedAreas(ast: AST.Arr): ISZ[InvalidatedAreas] = {
    var r = ISZ[InvalidatedAreas]()
    for (v <- ast.values) {
      r = r :+ toInvalidatedAreas(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toInvalidatedEventBody(ast: AST.Obj): InvalidatedEventBody = {
    val map = ast.asMap
    val areasOpt = map.getArrOpt("areas").map((o: AST.Arr) => toISZInvalidatedAreas(o))
    val threadIdOpt = map.getIntValueOpt("threadId")
    val stackFrameIdOpt = map.getIntValueOpt("stackFrameId")
    return InvalidatedEventBody(areasOpt, threadIdOpt, stackFrameIdOpt)
  }

  @pure def mkInvalidatedEventBody(
  ): InvalidatedEventBody = {
    return InvalidatedEventBody(None(), None(), None())
  }

  /*
    This event signals that some state in the debug adapter has changed and requires that the client needs to re-render the data snapshot previously requested.
    Debug adapters do not have to emit this event for runtime changes like stopped or thread events because in that case the client refetches the new state anyway. But the event can be used for example to refresh the UI after rendering formatting has changed in the debug adapter.
    This event should only be sent if the corresponding capability `supportsInvalidatedEvent` is true.
   */
  @datatype class InvalidatedEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          invalidated
        }
       */,
    val body: InvalidatedEventBody
  ) extends Event {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "invalidated")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toInvalidatedEvent(ast: AST.Obj): InvalidatedEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "invalidated")
    val body = toInvalidatedEventBody(map.getObj("body"))
    return InvalidatedEvent(seq, `type`, event, body)
  }

  @pure def mkInvalidatedEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    body: InvalidatedEventBody
  ): InvalidatedEvent = {
    return InvalidatedEvent(seq, "event", "invalidated", body)
  }

  @datatype class MemoryEventBody(
    val memoryReference: String /* Memory reference of a memory range that has been updated. */,
    val offset: Z /* Starting offset in bytes where memory has been updated. Can be negative. */,
    val count: Z /* Number of bytes updated. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("memoryReference", None()), AST.Str(memoryReference, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("offset", None()), AST.Int(offset, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("count", None()), AST.Int(count, None()))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toMemoryEventBody(ast: AST.Obj): MemoryEventBody = {
    val map = ast.asMap
    val memoryReference = map.getStr("memoryReference").value
    val offset = map.getInt("offset").value
    val count = map.getInt("count").value
    return MemoryEventBody(memoryReference, offset, count)
  }

  @pure def mkMemoryEventBody(
    memoryReference: String /* Memory reference of a memory range that has been updated. */,
    offset: Z /* Starting offset in bytes where memory has been updated. Can be negative. */,
    count: Z /* Number of bytes updated. */
  ): MemoryEventBody = {
    return MemoryEventBody(memoryReference, offset, count)
  }

  /*
    This event indicates that some memory range has been updated. It should only be sent if the corresponding capability `supportsMemoryEvent` is true.
    Clients typically react to the event by re-issuing a `readMemory` request if they show the memory identified by the `memoryReference` and if the updated memory range overlaps the displayed range. Clients should not make assumptions how individual memory references relate to each other, so they should not assume that they are part of a single continuous address range and might overlap.
    Debug adapters can use this event to indicate that the contents of a memory range has changed due to some other request like `setVariable` or `setExpression`. Debug adapters are not expected to emit this event for each and every memory change of a running program, because that information is typically not available from debuggers and it would flood clients with too many events.
   */
  @datatype class MemoryEvent(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          event
        }
       */,
    val event: String
      /*
        Has to be one of {
          memory
        }
       */,
    val body: MemoryEventBody
  ) extends Event {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "event")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(event == "memory")
      kvs = kvs :+ AST.KeyValue(AST.Str("event", None()), AST.Str(event, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toMemoryEvent(ast: AST.Obj): MemoryEvent = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "event")
    val event = map.getStr("event").value
    assert(event == "memory")
    val body = toMemoryEventBody(map.getObj("body"))
    return MemoryEvent(seq, `type`, event, body)
  }

  @pure def mkMemoryEvent(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    body: MemoryEventBody
  ): MemoryEvent = {
    return MemoryEvent(seq, "event", "memory", body)
  }

  /*
    This request is sent from the debug adapter to the client to run a command in a terminal.
    This is typically used to launch the debuggee in a terminal provided by the client.
    This request should only be called if the corresponding client capability `supportsRunInTerminalRequest` is true.
    Client implementations of `runInTerminal` are free to run the command however they choose including issuing the command to a command line interpreter (aka 'shell'). Argument strings passed to the `runInTerminal` request must arrive verbatim in the command to be run. As a consequence, clients which use a shell are responsible for escaping any special shell characters in the argument strings to prevent them from being interpreted (and modified) by the shell.
    Some users may wish to take advantage of shell processing in the argument strings. For clients which implement `runInTerminal` using an intermediary shell, the `argsCanBeInterpretedByShell` property can be set to true. In this case the client is requested not to escape any special shell characters in the argument strings.
   */
  @datatype class RunInTerminalRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          runInTerminal
        }
       */,
    val arguments: RunInTerminalRequestArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "runInTerminal")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toRunInTerminalRequest(ast: AST.Obj): RunInTerminalRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "runInTerminal")
    val arguments = toRunInTerminalRequestArguments(map.getObj("arguments"))
    return RunInTerminalRequest(seq, `type`, command, arguments)
  }

  @pure def mkRunInTerminalRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: RunInTerminalRequestArguments
  ): RunInTerminalRequest = {
    return RunInTerminalRequest(seq, "request", "runInTerminal", arguments)
  }

  @pure def fromISZString(seq: ISZ[String]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ AST.Str(v, None())
    }
    return AST.Arr(elements, None())
  }

  /* Arguments for `runInTerminal` request. */
  @datatype class RunInTerminalRequestArguments(
    val kindOpt: Option[String]
      /*
        What kind of terminal to launch. Defaults to `integrated` if not specified.
        Has to be one of {
          integrated,
          external
        }
       */,
    val titleOpt: Option[String] /* Title of the terminal. */,
    val cwd: String /* Working directory for the command. For non-empty, valid paths this typically results in execution of a change directory command. */,
    val args: ISZ[String] /* List of arguments. The first argument is the command to run. */,
    val envOpt: Option[`.Node`.Raw]
      /*
        Environment key-value pairs that are added to or removed from the default environment.
        Environment key-value pairs that are added to or removed from the default environment.
       */,
    val argsCanBeInterpretedByShellOpt: Option[B] /* This property should only be set if the corresponding capability `supportsArgsCanBeInterpretedByShell` is true. If the client uses an intermediary shell to launch the application, then the client must not attempt to escape characters with special meanings for the shell. The user is fully responsible for escaping as needed and that arguments using special characters may not be portable across shells. */
  ) extends `.Node` {
    @strictpure def kind: String = kindOpt.get
    @strictpure def title: String = titleOpt.get
    @strictpure def env: `.Node`.Raw = envOpt.get
    @strictpure def argsCanBeInterpretedByShell: B = argsCanBeInterpretedByShellOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      assert(kind == "integrated" || kind == "external")
      if (kindOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("kind", None()), AST.Str(kind, None()))
      }
      if (titleOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("title", None()), AST.Str(title, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("cwd", None()), AST.Str(cwd, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("args", None()), fromISZString(args))
      if (envOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("env", None()), env.value)
      }
      if (argsCanBeInterpretedByShellOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("argsCanBeInterpretedByShell", None()), AST.Bool(argsCanBeInterpretedByShell, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZString(ast: AST.Arr): ISZ[String] = {
    var r = ISZ[String]()
    for (v <- ast.values) {
      r = r :+ v.asInstanceOf[AST.Str].value
    }
    return r
  }

  @pure def toRunInTerminalRequestArguments(ast: AST.Obj): RunInTerminalRequestArguments = {
    val map = ast.asMap
    val kindOpt = map.getStrValueOpt("kind")
    kindOpt match {
      case Some(s) => assert(s == "integrated" || s == "external")
      case _ =>
    }
    val titleOpt = map.getStrValueOpt("title")
    val cwd = map.getStr("cwd").value
    val args = toISZString(map.getArr("args"))
    val envOpt = map.getOpt("env").map((o: AST) => `.Node`.Raw(o))
    val argsCanBeInterpretedByShellOpt = map.getBoolValueOpt("argsCanBeInterpretedByShell")
    return RunInTerminalRequestArguments(kindOpt, titleOpt, cwd, args, envOpt, argsCanBeInterpretedByShellOpt)
  }

  @pure def mkRunInTerminalRequestArguments(
    cwd: String /* Working directory for the command. For non-empty, valid paths this typically results in execution of a change directory command. */,
    args: ISZ[String] /* List of arguments. The first argument is the command to run. */
  ): RunInTerminalRequestArguments = {
    return RunInTerminalRequestArguments(None(), None(), cwd, args, None(), None())
  }

  @datatype class RunInTerminalResponseBody(
    val processIdOpt: Option[Z] /* The process ID. The value should be less than or equal to 2147483647 (2^31-1). */,
    val shellProcessIdOpt: Option[Z] /* The process ID of the terminal shell. The value should be less than or equal to 2147483647 (2^31-1). */
  ) extends `.Node` {
    @strictpure def processId: Z /* The process ID. The value should be less than or equal to 2147483647 (2^31-1). */ = processIdOpt.get
    @strictpure def shellProcessId: Z /* The process ID of the terminal shell. The value should be less than or equal to 2147483647 (2^31-1). */ = shellProcessIdOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (processIdOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("processId", None()), AST.Int(processId, None()))
      }
      if (shellProcessIdOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("shellProcessId", None()), AST.Int(shellProcessId, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toRunInTerminalResponseBody(ast: AST.Obj): RunInTerminalResponseBody = {
    val map = ast.asMap
    val processIdOpt = map.getIntValueOpt("processId")
    val shellProcessIdOpt = map.getIntValueOpt("shellProcessId")
    return RunInTerminalResponseBody(processIdOpt, shellProcessIdOpt)
  }

  @pure def mkRunInTerminalResponseBody(
  ): RunInTerminalResponseBody = {
    return RunInTerminalResponseBody(None(), None())
  }

  /* Response to `runInTerminal` request. */
  @datatype class RunInTerminalResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: RunInTerminalResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toRunInTerminalResponse(ast: AST.Obj): RunInTerminalResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toRunInTerminalResponseBody(map.getObj("body"))
    return RunInTerminalResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkRunInTerminalResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: RunInTerminalResponseBody
  ): RunInTerminalResponse = {
    return RunInTerminalResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    This request is sent from the debug adapter to the client to start a new debug session of the same type as the caller.
    This request should only be sent if the corresponding client capability `supportsStartDebuggingRequest` is true.
    A client implementation of `startDebugging` should start a new debug session (of the same type as the caller) in the same way that the caller's session was started. If the client supports hierarchical debug sessions, the newly created session can be treated as a child of the caller session.
   */
  @datatype class StartDebuggingRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          startDebugging
        }
       */,
    val arguments: StartDebuggingRequestArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "startDebugging")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStartDebuggingRequest(ast: AST.Obj): StartDebuggingRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "startDebugging")
    val arguments = toStartDebuggingRequestArguments(map.getObj("arguments"))
    return StartDebuggingRequest(seq, `type`, command, arguments)
  }

  @pure def mkStartDebuggingRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: StartDebuggingRequestArguments
  ): StartDebuggingRequest = {
    return StartDebuggingRequest(seq, "request", "startDebugging", arguments)
  }

  /* Arguments for `startDebugging` request. */
  @datatype class StartDebuggingRequestArguments(
    val configuration: `.Node`.Raw
      /*
        Arguments passed to the new debug session. The arguments must only contain properties understood by the `launch` or `attach` requests of the debug adapter and they must not contain any client-specific properties (e.g. `type`) or client-specific features (e.g. substitutable 'variables').
        Arguments passed to the new debug session. The arguments must only contain properties understood by the `launch` or `attach` requests of the debug adapter and they must not contain any client-specific properties (e.g. `type`) or client-specific features (e.g. substitutable 'variables').
       */,
    val request: String
      /*
        Indicates whether the new debug session should be started with a `launch` or `attach` request.
        Has to be one of {
          launch,
          attach
        }
       */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("configuration", None()), configuration.value)
      assert(request == "launch" || request == "attach")
      kvs = kvs :+ AST.KeyValue(AST.Str("request", None()), AST.Str(request, None()))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStartDebuggingRequestArguments(ast: AST.Obj): StartDebuggingRequestArguments = {
    val map = ast.asMap
    val configuration = `.Node`.Raw(map.get("configuration"))
    val request = map.getStr("request").value
    assert(request == "launch" || request == "attach")
    return StartDebuggingRequestArguments(configuration, request)
  }

  @pure def mkStartDebuggingRequestArguments(
    configuration: `.Node`.Raw
      /*
        Arguments passed to the new debug session. The arguments must only contain properties understood by the `launch` or `attach` requests of the debug adapter and they must not contain any client-specific properties (e.g. `type`) or client-specific features (e.g. substitutable 'variables').
        Arguments passed to the new debug session. The arguments must only contain properties understood by the `launch` or `attach` requests of the debug adapter and they must not contain any client-specific properties (e.g. `type`) or client-specific features (e.g. substitutable 'variables').
       */,
    request: String
      /*
        Indicates whether the new debug session should be started with a `launch` or `attach` request.
        Has to be one of {
          launch,
          attach
        }
       */
  ): StartDebuggingRequestArguments = {
    return StartDebuggingRequestArguments(configuration, request)
  }

  /* Response to `startDebugging` request. This is just an acknowledgement, so no body field is required. */
  @datatype class StartDebuggingResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStartDebuggingResponse(ast: AST.Obj): StartDebuggingResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return StartDebuggingResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkStartDebuggingResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): StartDebuggingResponse = {
    return StartDebuggingResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    The `initialize` request is sent as the first request from the client to the debug adapter in order to configure it with client capabilities and to retrieve capabilities from the debug adapter.
    Until the debug adapter has responded with an `initialize` response, the client must not send any additional requests or events to the debug adapter.
    In addition the debug adapter is not allowed to send any requests or events to the client until it has responded with an `initialize` response.
    The `initialize` request may only be sent once.
   */
  @datatype class InitializeRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          initialize
        }
       */,
    val arguments: InitializeRequestArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "initialize")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toInitializeRequest(ast: AST.Obj): InitializeRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "initialize")
    val arguments = toInitializeRequestArguments(map.getObj("arguments"))
    return InitializeRequest(seq, `type`, command, arguments)
  }

  @pure def mkInitializeRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: InitializeRequestArguments
  ): InitializeRequest = {
    return InitializeRequest(seq, "request", "initialize", arguments)
  }

  /* Arguments for `initialize` request. */
  @datatype class InitializeRequestArguments(
    val clientIDOpt: Option[String] /* The ID of the client using this adapter. */,
    val clientNameOpt: Option[String] /* The human-readable name of the client using this adapter. */,
    val adapterID: String /* The ID of the debug adapter. */,
    val localeOpt: Option[String] /* The ISO-639 locale of the client using this adapter, e.g. en-US or de-CH. */,
    val linesStartAt1Opt: Option[B] /* If true all line numbers are 1-based (default). */,
    val columnsStartAt1Opt: Option[B] /* If true all column numbers are 1-based (default). */,
    val pathFormatOpt: Option[String]
      /*
        Determines in what format paths are specified. The default is `path`, which is the native format.
        Has to be one of {
          path,
          uri
        }
       */,
    val supportsVariableTypeOpt: Option[B] /* Client supports the `type` attribute for variables. */,
    val supportsVariablePagingOpt: Option[B] /* Client supports the paging of variables. */,
    val supportsRunInTerminalRequestOpt: Option[B] /* Client supports the `runInTerminal` request. */,
    val supportsMemoryReferencesOpt: Option[B] /* Client supports memory references. */,
    val supportsProgressReportingOpt: Option[B] /* Client supports progress reporting. */,
    val supportsInvalidatedEventOpt: Option[B] /* Client supports the `invalidated` event. */,
    val supportsMemoryEventOpt: Option[B] /* Client supports the `memory` event. */,
    val supportsArgsCanBeInterpretedByShellOpt: Option[B] /* Client supports the `argsCanBeInterpretedByShell` attribute on the `runInTerminal` request. */,
    val supportsStartDebuggingRequestOpt: Option[B] /* Client supports the `startDebugging` request. */
  ) extends `.Node` {
    @strictpure def clientID: String = clientIDOpt.get
    @strictpure def clientName: String = clientNameOpt.get
    @strictpure def locale: String = localeOpt.get
    @strictpure def linesStartAt1: B = linesStartAt1Opt.get
    @strictpure def columnsStartAt1: B = columnsStartAt1Opt.get
    @strictpure def pathFormat: String = pathFormatOpt.get
    @strictpure def supportsVariableType: B = supportsVariableTypeOpt.get
    @strictpure def supportsVariablePaging: B = supportsVariablePagingOpt.get
    @strictpure def supportsRunInTerminalRequest: B = supportsRunInTerminalRequestOpt.get
    @strictpure def supportsMemoryReferences: B = supportsMemoryReferencesOpt.get
    @strictpure def supportsProgressReporting: B = supportsProgressReportingOpt.get
    @strictpure def supportsInvalidatedEvent: B = supportsInvalidatedEventOpt.get
    @strictpure def supportsMemoryEvent: B = supportsMemoryEventOpt.get
    @strictpure def supportsArgsCanBeInterpretedByShell: B = supportsArgsCanBeInterpretedByShellOpt.get
    @strictpure def supportsStartDebuggingRequest: B = supportsStartDebuggingRequestOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (clientIDOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("clientID", None()), AST.Str(clientID, None()))
      }
      if (clientNameOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("clientName", None()), AST.Str(clientName, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("adapterID", None()), AST.Str(adapterID, None()))
      if (localeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("locale", None()), AST.Str(locale, None()))
      }
      if (linesStartAt1Opt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("linesStartAt1", None()), AST.Bool(linesStartAt1, None()))
      }
      if (columnsStartAt1Opt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("columnsStartAt1", None()), AST.Bool(columnsStartAt1, None()))
      }
      assert(pathFormat == "path" || pathFormat == "uri")
      if (pathFormatOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("pathFormat", None()), AST.Str(pathFormat, None()))
      }
      if (supportsVariableTypeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsVariableType", None()), AST.Bool(supportsVariableType, None()))
      }
      if (supportsVariablePagingOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsVariablePaging", None()), AST.Bool(supportsVariablePaging, None()))
      }
      if (supportsRunInTerminalRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsRunInTerminalRequest", None()), AST.Bool(supportsRunInTerminalRequest, None()))
      }
      if (supportsMemoryReferencesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsMemoryReferences", None()), AST.Bool(supportsMemoryReferences, None()))
      }
      if (supportsProgressReportingOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsProgressReporting", None()), AST.Bool(supportsProgressReporting, None()))
      }
      if (supportsInvalidatedEventOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsInvalidatedEvent", None()), AST.Bool(supportsInvalidatedEvent, None()))
      }
      if (supportsMemoryEventOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsMemoryEvent", None()), AST.Bool(supportsMemoryEvent, None()))
      }
      if (supportsArgsCanBeInterpretedByShellOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsArgsCanBeInterpretedByShell", None()), AST.Bool(supportsArgsCanBeInterpretedByShell, None()))
      }
      if (supportsStartDebuggingRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsStartDebuggingRequest", None()), AST.Bool(supportsStartDebuggingRequest, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toInitializeRequestArguments(ast: AST.Obj): InitializeRequestArguments = {
    val map = ast.asMap
    val clientIDOpt = map.getStrValueOpt("clientID")
    val clientNameOpt = map.getStrValueOpt("clientName")
    val adapterID = map.getStr("adapterID").value
    val localeOpt = map.getStrValueOpt("locale")
    val linesStartAt1Opt = map.getBoolValueOpt("linesStartAt1")
    val columnsStartAt1Opt = map.getBoolValueOpt("columnsStartAt1")
    val pathFormatOpt = map.getStrValueOpt("pathFormat")
    pathFormatOpt match {
      case Some(s) => assert(s == "path" || s == "uri")
      case _ =>
    }
    val supportsVariableTypeOpt = map.getBoolValueOpt("supportsVariableType")
    val supportsVariablePagingOpt = map.getBoolValueOpt("supportsVariablePaging")
    val supportsRunInTerminalRequestOpt = map.getBoolValueOpt("supportsRunInTerminalRequest")
    val supportsMemoryReferencesOpt = map.getBoolValueOpt("supportsMemoryReferences")
    val supportsProgressReportingOpt = map.getBoolValueOpt("supportsProgressReporting")
    val supportsInvalidatedEventOpt = map.getBoolValueOpt("supportsInvalidatedEvent")
    val supportsMemoryEventOpt = map.getBoolValueOpt("supportsMemoryEvent")
    val supportsArgsCanBeInterpretedByShellOpt = map.getBoolValueOpt("supportsArgsCanBeInterpretedByShell")
    val supportsStartDebuggingRequestOpt = map.getBoolValueOpt("supportsStartDebuggingRequest")
    return InitializeRequestArguments(clientIDOpt, clientNameOpt, adapterID, localeOpt, linesStartAt1Opt, columnsStartAt1Opt, pathFormatOpt, supportsVariableTypeOpt, supportsVariablePagingOpt, supportsRunInTerminalRequestOpt, supportsMemoryReferencesOpt, supportsProgressReportingOpt, supportsInvalidatedEventOpt, supportsMemoryEventOpt, supportsArgsCanBeInterpretedByShellOpt, supportsStartDebuggingRequestOpt)
  }

  @pure def mkInitializeRequestArguments(
    adapterID: String /* The ID of the debug adapter. */
  ): InitializeRequestArguments = {
    return InitializeRequestArguments(None(), None(), adapterID, None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None())
  }

  /* Response to `initialize` request. */
  @datatype class InitializeResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[Capabilities] /* The capabilities of this debug adapter. */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: Capabilities = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toInitializeResponse(ast: AST.Obj): InitializeResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getObjOpt("body").map((o: AST.Obj) => toCapabilities(o))
    return InitializeResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkInitializeResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): InitializeResponse = {
    return InitializeResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    This request indicates that the client has finished initialization of the debug adapter.
    So it is the last request in the sequence of configuration requests (which was started by the `initialized` event).
    Clients should only call this request if the corresponding capability `supportsConfigurationDoneRequest` is true.
   */
  @datatype class ConfigurationDoneRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          configurationDone
        }
       */,
    val argumentsOpt: Option[ConfigurationDoneArguments]
  ) extends Request {
    @strictpure def arguments: ConfigurationDoneArguments = argumentsOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "configurationDone")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      if (argumentsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toConfigurationDoneRequest(ast: AST.Obj): ConfigurationDoneRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "configurationDone")
    val argumentsOpt = map.getObjOpt("arguments").map((o: AST.Obj) => toConfigurationDoneArguments(o))
    return ConfigurationDoneRequest(seq, `type`, command, argumentsOpt)
  }

  @pure def mkConfigurationDoneRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */
  ): ConfigurationDoneRequest = {
    return ConfigurationDoneRequest(seq, "request", "configurationDone", None())
  }

  /* Arguments for `configurationDone` request. */
  @datatype class ConfigurationDoneArguments(
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      return AST.Obj(kvs, None())
    }
  }

  @pure def toConfigurationDoneArguments(ast: AST.Obj): ConfigurationDoneArguments = {
    val map = ast.asMap
    return ConfigurationDoneArguments()
  }

  @pure def mkConfigurationDoneArguments(
  ): ConfigurationDoneArguments = {
    return ConfigurationDoneArguments()
  }

  /* Response to `configurationDone` request. This is just an acknowledgement, so no body field is required. */
  @datatype class ConfigurationDoneResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toConfigurationDoneResponse(ast: AST.Obj): ConfigurationDoneResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return ConfigurationDoneResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkConfigurationDoneResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): ConfigurationDoneResponse = {
    return ConfigurationDoneResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    This launch request is sent from the client to the debug adapter to start the debuggee with or without debugging (if `noDebug` is true).
    Since launching is debugger/runtime specific, the arguments for this request are not part of this specification.
   */
  @datatype class LaunchRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          launch
        }
       */,
    val arguments: LaunchRequestArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "launch")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toLaunchRequest(ast: AST.Obj): LaunchRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "launch")
    val arguments = toLaunchRequestArguments(map.getObj("arguments"))
    return LaunchRequest(seq, `type`, command, arguments)
  }

  @pure def mkLaunchRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: LaunchRequestArguments
  ): LaunchRequest = {
    return LaunchRequest(seq, "request", "launch", arguments)
  }

  /* Arguments for `launch` request. Additional attributes are implementation specific. */
  @datatype class LaunchRequestArguments(
    val noDebugOpt: Option[B] /* If true, the launch request should launch the program without enabling debugging. */,
    val __restartOpt: Option[`.Node`]
      /*
        Arbitrary data from the previous, restarted session.
        The data is sent as the `restart` attribute of the `terminated` event.
        The client should leave the data intact.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends `.Node` {
    @strictpure def noDebug: B = noDebugOpt.get
    @strictpure def __restart: `.Node` = __restartOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (noDebugOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("noDebug", None()), AST.Bool(noDebug, None()))
      }
      if (__restartOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("__restart", None()), __restart.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toLaunchRequestArguments(ast: AST.Obj): LaunchRequestArguments = {
    val map = ast.asMap
    val noDebugOpt = map.getBoolValueOpt("noDebug")
    val __restartOpt = map.getOpt("__restart").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return LaunchRequestArguments(noDebugOpt, __restartOpt)
  }

  @pure def mkLaunchRequestArguments(
  ): LaunchRequestArguments = {
    return LaunchRequestArguments(None(), None())
  }

  /* Response to `launch` request. This is just an acknowledgement, so no body field is required. */
  @datatype class LaunchResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toLaunchResponse(ast: AST.Obj): LaunchResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return LaunchResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkLaunchResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): LaunchResponse = {
    return LaunchResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    The `attach` request is sent from the client to the debug adapter to attach to a debuggee that is already running.
    Since attaching is debugger/runtime specific, the arguments for this request are not part of this specification.
   */
  @datatype class AttachRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          attach
        }
       */,
    val arguments: AttachRequestArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "attach")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toAttachRequest(ast: AST.Obj): AttachRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "attach")
    val arguments = toAttachRequestArguments(map.getObj("arguments"))
    return AttachRequest(seq, `type`, command, arguments)
  }

  @pure def mkAttachRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: AttachRequestArguments
  ): AttachRequest = {
    return AttachRequest(seq, "request", "attach", arguments)
  }

  /* Arguments for `attach` request. Additional attributes are implementation specific. */
  @datatype class AttachRequestArguments(
    val __restartOpt: Option[`.Node`]
      /*
        Arbitrary data from the previous, restarted session.
        The data is sent as the `restart` attribute of the `terminated` event.
        The client should leave the data intact.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends `.Node` {
    @strictpure def __restart: `.Node` = __restartOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (__restartOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("__restart", None()), __restart.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toAttachRequestArguments(ast: AST.Obj): AttachRequestArguments = {
    val map = ast.asMap
    val __restartOpt = map.getOpt("__restart").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return AttachRequestArguments(__restartOpt)
  }

  @pure def mkAttachRequestArguments(
  ): AttachRequestArguments = {
    return AttachRequestArguments(None())
  }

  /* Response to `attach` request. This is just an acknowledgement, so no body field is required. */
  @datatype class AttachResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toAttachResponse(ast: AST.Obj): AttachResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return AttachResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkAttachResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): AttachResponse = {
    return AttachResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    Restarts a debug session. Clients should only call this request if the corresponding capability `supportsRestartRequest` is true.
    If the capability is missing or has the value false, a typical client emulates `restart` by terminating the debug adapter first and then launching it anew.
   */
  @datatype class RestartRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          restart
        }
       */,
    val argumentsOpt: Option[RestartArguments]
  ) extends Request {
    @strictpure def arguments: RestartArguments = argumentsOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "restart")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      if (argumentsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toRestartRequest(ast: AST.Obj): RestartRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "restart")
    val argumentsOpt = map.getObjOpt("arguments").map((o: AST.Obj) => toRestartArguments(o))
    return RestartRequest(seq, `type`, command, argumentsOpt)
  }

  @pure def mkRestartRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */
  ): RestartRequest = {
    return RestartRequest(seq, "request", "restart", None())
  }

  /* Arguments for `restart` request. */
  @datatype class RestartArguments(
    val argumentsOpt: Option[`.Node`.Raw] /* The latest version of the `launch` or `attach` configuration. */
  ) extends `.Node` {
    @strictpure def arguments: `.Node`.Raw = argumentsOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (argumentsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.value)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toRestartArguments(ast: AST.Obj): RestartArguments = {
    val map = ast.asMap
    val argumentsOpt = map.getOpt("arguments").map((o: AST) => `.Node`.Raw(o))
    return RestartArguments(argumentsOpt)
  }

  @pure def mkRestartArguments(
  ): RestartArguments = {
    return RestartArguments(None())
  }

  /* Response to `restart` request. This is just an acknowledgement, so no body field is required. */
  @datatype class RestartResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toRestartResponse(ast: AST.Obj): RestartResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return RestartResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkRestartResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): RestartResponse = {
    return RestartResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    The `disconnect` request asks the debug adapter to disconnect from the debuggee (thus ending the debug session) and then to shut down itself (the debug adapter).
    In addition, the debug adapter must terminate the debuggee if it was started with the `launch` request. If an `attach` request was used to connect to the debuggee, then the debug adapter must not terminate the debuggee.
    This implicit behavior of when to terminate the debuggee can be overridden with the `terminateDebuggee` argument (which is only supported by a debug adapter if the corresponding capability `supportTerminateDebuggee` is true).
   */
  @datatype class DisconnectRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          disconnect
        }
       */,
    val argumentsOpt: Option[DisconnectArguments]
  ) extends Request {
    @strictpure def arguments: DisconnectArguments = argumentsOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "disconnect")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      if (argumentsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toDisconnectRequest(ast: AST.Obj): DisconnectRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "disconnect")
    val argumentsOpt = map.getObjOpt("arguments").map((o: AST.Obj) => toDisconnectArguments(o))
    return DisconnectRequest(seq, `type`, command, argumentsOpt)
  }

  @pure def mkDisconnectRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */
  ): DisconnectRequest = {
    return DisconnectRequest(seq, "request", "disconnect", None())
  }

  /* Arguments for `disconnect` request. */
  @datatype class DisconnectArguments(
    val restartOpt: Option[B] /* A value of true indicates that this `disconnect` request is part of a restart sequence. */,
    val terminateDebuggeeOpt: Option[B]
      /*
        Indicates whether the debuggee should be terminated when the debugger is disconnected.
        If unspecified, the debug adapter is free to do whatever it thinks is best.
        The attribute is only honored by a debug adapter if the corresponding capability `supportTerminateDebuggee` is true.
       */,
    val suspendDebuggeeOpt: Option[B]
      /*
        Indicates whether the debuggee should stay suspended when the debugger is disconnected.
        If unspecified, the debuggee should resume execution.
        The attribute is only honored by a debug adapter if the corresponding capability `supportSuspendDebuggee` is true.
       */
  ) extends `.Node` {
    @strictpure def restart: B = restartOpt.get
    @strictpure def terminateDebuggee: B = terminateDebuggeeOpt.get
    @strictpure def suspendDebuggee: B = suspendDebuggeeOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (restartOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("restart", None()), AST.Bool(restart, None()))
      }
      if (terminateDebuggeeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("terminateDebuggee", None()), AST.Bool(terminateDebuggee, None()))
      }
      if (suspendDebuggeeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("suspendDebuggee", None()), AST.Bool(suspendDebuggee, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toDisconnectArguments(ast: AST.Obj): DisconnectArguments = {
    val map = ast.asMap
    val restartOpt = map.getBoolValueOpt("restart")
    val terminateDebuggeeOpt = map.getBoolValueOpt("terminateDebuggee")
    val suspendDebuggeeOpt = map.getBoolValueOpt("suspendDebuggee")
    return DisconnectArguments(restartOpt, terminateDebuggeeOpt, suspendDebuggeeOpt)
  }

  @pure def mkDisconnectArguments(
  ): DisconnectArguments = {
    return DisconnectArguments(None(), None(), None())
  }

  /* Response to `disconnect` request. This is just an acknowledgement, so no body field is required. */
  @datatype class DisconnectResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toDisconnectResponse(ast: AST.Obj): DisconnectResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return DisconnectResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkDisconnectResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): DisconnectResponse = {
    return DisconnectResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    The `terminate` request is sent from the client to the debug adapter in order to shut down the debuggee gracefully. Clients should only call this request if the capability `supportsTerminateRequest` is true.
    Typically a debug adapter implements `terminate` by sending a software signal which the debuggee intercepts in order to clean things up properly before terminating itself.
    Please note that this request does not directly affect the state of the debug session: if the debuggee decides to veto the graceful shutdown for any reason by not terminating itself, then the debug session just continues.
    Clients can surface the `terminate` request as an explicit command or they can integrate it into a two stage Stop command that first sends `terminate` to request a graceful shutdown, and if that fails uses `disconnect` for a forceful shutdown.
   */
  @datatype class TerminateRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          terminate
        }
       */,
    val argumentsOpt: Option[TerminateArguments]
  ) extends Request {
    @strictpure def arguments: TerminateArguments = argumentsOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "terminate")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      if (argumentsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toTerminateRequest(ast: AST.Obj): TerminateRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "terminate")
    val argumentsOpt = map.getObjOpt("arguments").map((o: AST.Obj) => toTerminateArguments(o))
    return TerminateRequest(seq, `type`, command, argumentsOpt)
  }

  @pure def mkTerminateRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */
  ): TerminateRequest = {
    return TerminateRequest(seq, "request", "terminate", None())
  }

  /* Arguments for `terminate` request. */
  @datatype class TerminateArguments(
    val restartOpt: Option[B] /* A value of true indicates that this `terminate` request is part of a restart sequence. */
  ) extends `.Node` {
    @strictpure def restart: B = restartOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (restartOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("restart", None()), AST.Bool(restart, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toTerminateArguments(ast: AST.Obj): TerminateArguments = {
    val map = ast.asMap
    val restartOpt = map.getBoolValueOpt("restart")
    return TerminateArguments(restartOpt)
  }

  @pure def mkTerminateArguments(
  ): TerminateArguments = {
    return TerminateArguments(None())
  }

  /* Response to `terminate` request. This is just an acknowledgement, so no body field is required. */
  @datatype class TerminateResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toTerminateResponse(ast: AST.Obj): TerminateResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return TerminateResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkTerminateResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): TerminateResponse = {
    return TerminateResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    The `breakpointLocations` request returns all possible locations for source breakpoints in a given range.
    Clients should only call this request if the corresponding capability `supportsBreakpointLocationsRequest` is true.
   */
  @datatype class BreakpointLocationsRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          breakpointLocations
        }
       */,
    val argumentsOpt: Option[BreakpointLocationsArguments]
  ) extends Request {
    @strictpure def arguments: BreakpointLocationsArguments = argumentsOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "breakpointLocations")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      if (argumentsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toBreakpointLocationsRequest(ast: AST.Obj): BreakpointLocationsRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "breakpointLocations")
    val argumentsOpt = map.getObjOpt("arguments").map((o: AST.Obj) => toBreakpointLocationsArguments(o))
    return BreakpointLocationsRequest(seq, `type`, command, argumentsOpt)
  }

  @pure def mkBreakpointLocationsRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */
  ): BreakpointLocationsRequest = {
    return BreakpointLocationsRequest(seq, "request", "breakpointLocations", None())
  }

  /* Arguments for `breakpointLocations` request. */
  @datatype class BreakpointLocationsArguments(
    val source: Source /* The source location of the breakpoints; either `source.path` or `source.sourceReference` must be specified. */,
    val line: Z /* Start line of range to search possible breakpoint locations in. If only the line is specified, the request returns all possible locations in that line. */,
    val columnOpt: Option[Z] /* Start position within `line` to search possible breakpoint locations in. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. If no column is given, the first position in the start line is assumed. */,
    val endLineOpt: Option[Z] /* End line of range to search possible breakpoint locations in. If no end line is given, then the end line is assumed to be the start line. */,
    val endColumnOpt: Option[Z] /* End position within `endLine` to search possible breakpoint locations in. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. If no end column is given, the last position in the end line is assumed. */
  ) extends `.Node` {
    @strictpure def column: Z = columnOpt.get
    @strictpure def endLine: Z = endLineOpt.get
    @strictpure def endColumn: Z = endColumnOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("source", None()), source.toAST)
      kvs = kvs :+ AST.KeyValue(AST.Str("line", None()), AST.Int(line, None()))
      if (columnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("column", None()), AST.Int(column, None()))
      }
      if (endLineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endLine", None()), AST.Int(endLine, None()))
      }
      if (endColumnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endColumn", None()), AST.Int(endColumn, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toBreakpointLocationsArguments(ast: AST.Obj): BreakpointLocationsArguments = {
    val map = ast.asMap
    val source = toSource(map.getObj("source"))
    val line = map.getInt("line").value
    val columnOpt = map.getIntValueOpt("column")
    val endLineOpt = map.getIntValueOpt("endLine")
    val endColumnOpt = map.getIntValueOpt("endColumn")
    return BreakpointLocationsArguments(source, line, columnOpt, endLineOpt, endColumnOpt)
  }

  @pure def mkBreakpointLocationsArguments(
    source: Source /* The source location of the breakpoints; either `source.path` or `source.sourceReference` must be specified. */,
    line: Z /* Start line of range to search possible breakpoint locations in. If only the line is specified, the request returns all possible locations in that line. */
  ): BreakpointLocationsArguments = {
    return BreakpointLocationsArguments(source, line, None(), None(), None())
  }

  @pure def fromISZBreakpointLocation(seq: ISZ[BreakpointLocation]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @datatype class BreakpointLocationsResponseBody(
    val breakpoints: ISZ[BreakpointLocation] /* Sorted set of possible breakpoint locations. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("breakpoints", None()), fromISZBreakpointLocation(breakpoints))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZBreakpointLocation(ast: AST.Arr): ISZ[BreakpointLocation] = {
    var r = ISZ[BreakpointLocation]()
    for (v <- ast.values) {
      r = r :+ toBreakpointLocation(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toBreakpointLocationsResponseBody(ast: AST.Obj): BreakpointLocationsResponseBody = {
    val map = ast.asMap
    val breakpoints = toISZBreakpointLocation(map.getArr("breakpoints"))
    return BreakpointLocationsResponseBody(breakpoints)
  }

  @pure def mkBreakpointLocationsResponseBody(
    breakpoints: ISZ[BreakpointLocation] /* Sorted set of possible breakpoint locations. */
  ): BreakpointLocationsResponseBody = {
    return BreakpointLocationsResponseBody(breakpoints)
  }

  /*
    Response to `breakpointLocations` request.
    Contains possible locations for source breakpoints.
   */
  @datatype class BreakpointLocationsResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: BreakpointLocationsResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toBreakpointLocationsResponse(ast: AST.Obj): BreakpointLocationsResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toBreakpointLocationsResponseBody(map.getObj("body"))
    return BreakpointLocationsResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkBreakpointLocationsResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: BreakpointLocationsResponseBody
  ): BreakpointLocationsResponse = {
    return BreakpointLocationsResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    Sets multiple breakpoints for a single source and clears all previous breakpoints in that source.
    To clear all breakpoint for a source, specify an empty array.
    When a breakpoint is hit, a `stopped` event (with reason `breakpoint`) is generated.
   */
  @datatype class SetBreakpointsRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          setBreakpoints
        }
       */,
    val arguments: SetBreakpointsArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "setBreakpoints")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetBreakpointsRequest(ast: AST.Obj): SetBreakpointsRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "setBreakpoints")
    val arguments = toSetBreakpointsArguments(map.getObj("arguments"))
    return SetBreakpointsRequest(seq, `type`, command, arguments)
  }

  @pure def mkSetBreakpointsRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: SetBreakpointsArguments
  ): SetBreakpointsRequest = {
    return SetBreakpointsRequest(seq, "request", "setBreakpoints", arguments)
  }

  @pure def fromISZSourceBreakpoint(seq: ISZ[SourceBreakpoint]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  /* Arguments for `setBreakpoints` request. */
  @datatype class SetBreakpointsArguments(
    val source: Source /* The source location of the breakpoints; either `source.path` or `source.sourceReference` must be specified. */,
    val breakpointsOpt: Option[ISZ[SourceBreakpoint]] /* The code locations of the breakpoints. */,
    val linesOpt: Option[ISZ[Z]] /* Deprecated: The code locations of the breakpoints. */,
    val sourceModifiedOpt: Option[B] /* A value of true indicates that the underlying source has been modified which results in new breakpoint locations. */
  ) extends `.Node` {
    @strictpure def breakpoints: ISZ[SourceBreakpoint] = breakpointsOpt.get
    @strictpure def lines: ISZ[Z] = linesOpt.get
    @strictpure def sourceModified: B = sourceModifiedOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("source", None()), source.toAST)
      if (breakpointsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("breakpoints", None()), fromISZSourceBreakpoint(breakpoints))
      }
      if (linesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("lines", None()), fromISZZ(lines))
      }
      if (sourceModifiedOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("sourceModified", None()), AST.Bool(sourceModified, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZSourceBreakpoint(ast: AST.Arr): ISZ[SourceBreakpoint] = {
    var r = ISZ[SourceBreakpoint]()
    for (v <- ast.values) {
      r = r :+ toSourceBreakpoint(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toSetBreakpointsArguments(ast: AST.Obj): SetBreakpointsArguments = {
    val map = ast.asMap
    val source = toSource(map.getObj("source"))
    val breakpointsOpt = map.getArrOpt("breakpoints").map((o: AST.Arr) => toISZSourceBreakpoint(o))
    val linesOpt = map.getArrOpt("lines").map((o: AST.Arr) => toISZZ(o))
    val sourceModifiedOpt = map.getBoolValueOpt("sourceModified")
    return SetBreakpointsArguments(source, breakpointsOpt, linesOpt, sourceModifiedOpt)
  }

  @pure def mkSetBreakpointsArguments(
    source: Source /* The source location of the breakpoints; either `source.path` or `source.sourceReference` must be specified. */
  ): SetBreakpointsArguments = {
    return SetBreakpointsArguments(source, None(), None(), None())
  }

  @pure def fromISZBreakpoint(seq: ISZ[Breakpoint]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @datatype class SetBreakpointsResponseBody(
    val breakpoints: ISZ[Breakpoint]
      /*
        Information about the breakpoints.
        The array elements are in the same order as the elements of the `breakpoints` (or the deprecated `lines`) array in the arguments.
       */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("breakpoints", None()), fromISZBreakpoint(breakpoints))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZBreakpoint(ast: AST.Arr): ISZ[Breakpoint] = {
    var r = ISZ[Breakpoint]()
    for (v <- ast.values) {
      r = r :+ toBreakpoint(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toSetBreakpointsResponseBody(ast: AST.Obj): SetBreakpointsResponseBody = {
    val map = ast.asMap
    val breakpoints = toISZBreakpoint(map.getArr("breakpoints"))
    return SetBreakpointsResponseBody(breakpoints)
  }

  @pure def mkSetBreakpointsResponseBody(
    breakpoints: ISZ[Breakpoint]
      /*
        Information about the breakpoints.
        The array elements are in the same order as the elements of the `breakpoints` (or the deprecated `lines`) array in the arguments.
       */
  ): SetBreakpointsResponseBody = {
    return SetBreakpointsResponseBody(breakpoints)
  }

  /*
    Response to `setBreakpoints` request.
    Returned is information about each breakpoint created by this request.
    This includes the actual code location and whether the breakpoint could be verified.
    The breakpoints returned are in the same order as the elements of the `breakpoints`
    (or the deprecated `lines`) array in the arguments.
   */
  @datatype class SetBreakpointsResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: SetBreakpointsResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetBreakpointsResponse(ast: AST.Obj): SetBreakpointsResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toSetBreakpointsResponseBody(map.getObj("body"))
    return SetBreakpointsResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkSetBreakpointsResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: SetBreakpointsResponseBody
  ): SetBreakpointsResponse = {
    return SetBreakpointsResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    Replaces all existing function breakpoints with new function breakpoints.
    To clear all function breakpoints, specify an empty array.
    When a function breakpoint is hit, a `stopped` event (with reason `function breakpoint`) is generated.
    Clients should only call this request if the corresponding capability `supportsFunctionBreakpoints` is true.
   */
  @datatype class SetFunctionBreakpointsRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          setFunctionBreakpoints
        }
       */,
    val arguments: SetFunctionBreakpointsArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "setFunctionBreakpoints")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetFunctionBreakpointsRequest(ast: AST.Obj): SetFunctionBreakpointsRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "setFunctionBreakpoints")
    val arguments = toSetFunctionBreakpointsArguments(map.getObj("arguments"))
    return SetFunctionBreakpointsRequest(seq, `type`, command, arguments)
  }

  @pure def mkSetFunctionBreakpointsRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: SetFunctionBreakpointsArguments
  ): SetFunctionBreakpointsRequest = {
    return SetFunctionBreakpointsRequest(seq, "request", "setFunctionBreakpoints", arguments)
  }

  @pure def fromISZFunctionBreakpoint(seq: ISZ[FunctionBreakpoint]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  /* Arguments for `setFunctionBreakpoints` request. */
  @datatype class SetFunctionBreakpointsArguments(
    val breakpoints: ISZ[FunctionBreakpoint] /* The function names of the breakpoints. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("breakpoints", None()), fromISZFunctionBreakpoint(breakpoints))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZFunctionBreakpoint(ast: AST.Arr): ISZ[FunctionBreakpoint] = {
    var r = ISZ[FunctionBreakpoint]()
    for (v <- ast.values) {
      r = r :+ toFunctionBreakpoint(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toSetFunctionBreakpointsArguments(ast: AST.Obj): SetFunctionBreakpointsArguments = {
    val map = ast.asMap
    val breakpoints = toISZFunctionBreakpoint(map.getArr("breakpoints"))
    return SetFunctionBreakpointsArguments(breakpoints)
  }

  @pure def mkSetFunctionBreakpointsArguments(
    breakpoints: ISZ[FunctionBreakpoint] /* The function names of the breakpoints. */
  ): SetFunctionBreakpointsArguments = {
    return SetFunctionBreakpointsArguments(breakpoints)
  }

  @datatype class SetFunctionBreakpointsResponseBody(
    val breakpoints: ISZ[Breakpoint] /* Information about the breakpoints. The array elements correspond to the elements of the `breakpoints` array. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("breakpoints", None()), fromISZBreakpoint(breakpoints))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetFunctionBreakpointsResponseBody(ast: AST.Obj): SetFunctionBreakpointsResponseBody = {
    val map = ast.asMap
    val breakpoints = toISZBreakpoint(map.getArr("breakpoints"))
    return SetFunctionBreakpointsResponseBody(breakpoints)
  }

  @pure def mkSetFunctionBreakpointsResponseBody(
    breakpoints: ISZ[Breakpoint] /* Information about the breakpoints. The array elements correspond to the elements of the `breakpoints` array. */
  ): SetFunctionBreakpointsResponseBody = {
    return SetFunctionBreakpointsResponseBody(breakpoints)
  }

  /*
    Response to `setFunctionBreakpoints` request.
    Returned is information about each breakpoint created by this request.
   */
  @datatype class SetFunctionBreakpointsResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: SetFunctionBreakpointsResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetFunctionBreakpointsResponse(ast: AST.Obj): SetFunctionBreakpointsResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toSetFunctionBreakpointsResponseBody(map.getObj("body"))
    return SetFunctionBreakpointsResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkSetFunctionBreakpointsResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: SetFunctionBreakpointsResponseBody
  ): SetFunctionBreakpointsResponse = {
    return SetFunctionBreakpointsResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    The request configures the debugger's response to thrown exceptions. Each of the `filters`, `filterOptions`, and `exceptionOptions` in the request are independent configurations to a debug adapter indicating a kind of exception to catch. An exception thrown in a program should result in a `stopped` event from the debug adapter (with reason `exception`) if any of the configured filters match.
    Clients should only call this request if the corresponding capability `exceptionBreakpointFilters` returns one or more filters.
   */
  @datatype class SetExceptionBreakpointsRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          setExceptionBreakpoints
        }
       */,
    val arguments: SetExceptionBreakpointsArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "setExceptionBreakpoints")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetExceptionBreakpointsRequest(ast: AST.Obj): SetExceptionBreakpointsRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "setExceptionBreakpoints")
    val arguments = toSetExceptionBreakpointsArguments(map.getObj("arguments"))
    return SetExceptionBreakpointsRequest(seq, `type`, command, arguments)
  }

  @pure def mkSetExceptionBreakpointsRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: SetExceptionBreakpointsArguments
  ): SetExceptionBreakpointsRequest = {
    return SetExceptionBreakpointsRequest(seq, "request", "setExceptionBreakpoints", arguments)
  }

  @pure def fromISZExceptionFilterOptions(seq: ISZ[ExceptionFilterOptions]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @pure def fromISZExceptionOptions(seq: ISZ[ExceptionOptions]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  /* Arguments for `setExceptionBreakpoints` request. */
  @datatype class SetExceptionBreakpointsArguments(
    val filters: ISZ[String] /* Set of exception filters specified by their ID. The set of all possible exception filters is defined by the `exceptionBreakpointFilters` capability. The `filter` and `filterOptions` sets are additive. */,
    val filterOptionsOpt: Option[ISZ[ExceptionFilterOptions]] /* Set of exception filters and their options. The set of all possible exception filters is defined by the `exceptionBreakpointFilters` capability. This attribute is only honored by a debug adapter if the corresponding capability `supportsExceptionFilterOptions` is true. The `filter` and `filterOptions` sets are additive. */,
    val exceptionOptionsOpt: Option[ISZ[ExceptionOptions]]
      /*
        Configuration options for selected exceptions.
        The attribute is only honored by a debug adapter if the corresponding capability `supportsExceptionOptions` is true.
       */
  ) extends `.Node` {
    @strictpure def filterOptions: ISZ[ExceptionFilterOptions] = filterOptionsOpt.get
    @strictpure def exceptionOptions: ISZ[ExceptionOptions] = exceptionOptionsOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("filters", None()), fromISZString(filters))
      if (filterOptionsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("filterOptions", None()), fromISZExceptionFilterOptions(filterOptions))
      }
      if (exceptionOptionsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("exceptionOptions", None()), fromISZExceptionOptions(exceptionOptions))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZExceptionFilterOptions(ast: AST.Arr): ISZ[ExceptionFilterOptions] = {
    var r = ISZ[ExceptionFilterOptions]()
    for (v <- ast.values) {
      r = r :+ toExceptionFilterOptions(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toISZExceptionOptions(ast: AST.Arr): ISZ[ExceptionOptions] = {
    var r = ISZ[ExceptionOptions]()
    for (v <- ast.values) {
      r = r :+ toExceptionOptions(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toSetExceptionBreakpointsArguments(ast: AST.Obj): SetExceptionBreakpointsArguments = {
    val map = ast.asMap
    val filters = toISZString(map.getArr("filters"))
    val filterOptionsOpt = map.getArrOpt("filterOptions").map((o: AST.Arr) => toISZExceptionFilterOptions(o))
    val exceptionOptionsOpt = map.getArrOpt("exceptionOptions").map((o: AST.Arr) => toISZExceptionOptions(o))
    return SetExceptionBreakpointsArguments(filters, filterOptionsOpt, exceptionOptionsOpt)
  }

  @pure def mkSetExceptionBreakpointsArguments(
    filters: ISZ[String] /* Set of exception filters specified by their ID. The set of all possible exception filters is defined by the `exceptionBreakpointFilters` capability. The `filter` and `filterOptions` sets are additive. */
  ): SetExceptionBreakpointsArguments = {
    return SetExceptionBreakpointsArguments(filters, None(), None())
  }

  @datatype class SetExceptionBreakpointsResponseBody(
    val breakpointsOpt: Option[ISZ[Breakpoint]]
      /*
        Information about the exception breakpoints or filters.
        The breakpoints returned are in the same order as the elements of the `filters`, `filterOptions`, `exceptionOptions` arrays in the arguments. If both `filters` and `filterOptions` are given, the returned array must start with `filters` information first, followed by `filterOptions` information.
       */
  ) extends `.Node` {
    @strictpure def breakpoints: ISZ[Breakpoint]
      /*
        Information about the exception breakpoints or filters.
        The breakpoints returned are in the same order as the elements of the `filters`, `filterOptions`, `exceptionOptions` arrays in the arguments. If both `filters` and `filterOptions` are given, the returned array must start with `filters` information first, followed by `filterOptions` information.
       */ = breakpointsOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (breakpointsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("breakpoints", None()), fromISZBreakpoint(breakpoints))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetExceptionBreakpointsResponseBody(ast: AST.Obj): SetExceptionBreakpointsResponseBody = {
    val map = ast.asMap
    val breakpointsOpt = map.getArrOpt("breakpoints").map((o: AST.Arr) => toISZBreakpoint(o))
    return SetExceptionBreakpointsResponseBody(breakpointsOpt)
  }

  @pure def mkSetExceptionBreakpointsResponseBody(
  ): SetExceptionBreakpointsResponseBody = {
    return SetExceptionBreakpointsResponseBody(None())
  }

  /*
    Response to `setExceptionBreakpoints` request.
    The response contains an array of `Breakpoint` objects with information about each exception breakpoint or filter. The `Breakpoint` objects are in the same order as the elements of the `filters`, `filterOptions`, `exceptionOptions` arrays given as arguments. If both `filters` and `filterOptions` are given, the returned array must start with `filters` information first, followed by `filterOptions` information.
    The `verified` property of a `Breakpoint` object signals whether the exception breakpoint or filter could be successfully created and whether the condition is valid. In case of an error the `message` property explains the problem. The `id` property can be used to introduce a unique ID for the exception breakpoint or filter so that it can be updated subsequently by sending breakpoint events.
    For backward compatibility both the `breakpoints` array and the enclosing `body` are optional. If these elements are missing a client is not able to show problems for individual exception breakpoints or filters.
   */
  @datatype class SetExceptionBreakpointsResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[SetExceptionBreakpointsResponseBody]
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: SetExceptionBreakpointsResponseBody = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetExceptionBreakpointsResponse(ast: AST.Obj): SetExceptionBreakpointsResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getObjOpt("body").map((o: AST.Obj) => toSetExceptionBreakpointsResponseBody(o))
    return SetExceptionBreakpointsResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkSetExceptionBreakpointsResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): SetExceptionBreakpointsResponse = {
    return SetExceptionBreakpointsResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    Obtains information on a possible data breakpoint that could be set on an expression or variable.
    Clients should only call this request if the corresponding capability `supportsDataBreakpoints` is true.
   */
  @datatype class DataBreakpointInfoRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          dataBreakpointInfo
        }
       */,
    val arguments: DataBreakpointInfoArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "dataBreakpointInfo")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toDataBreakpointInfoRequest(ast: AST.Obj): DataBreakpointInfoRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "dataBreakpointInfo")
    val arguments = toDataBreakpointInfoArguments(map.getObj("arguments"))
    return DataBreakpointInfoRequest(seq, `type`, command, arguments)
  }

  @pure def mkDataBreakpointInfoRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: DataBreakpointInfoArguments
  ): DataBreakpointInfoRequest = {
    return DataBreakpointInfoRequest(seq, "request", "dataBreakpointInfo", arguments)
  }

  /* Arguments for `dataBreakpointInfo` request. */
  @datatype class DataBreakpointInfoArguments(
    val variablesReferenceOpt: Option[Z] /* Reference to the variable container if the data breakpoint is requested for a child of the container. The `variablesReference` must have been obtained in the current suspended state. See 'Lifetime of Object References' in the Overview section for details. */,
    val name: String
      /*
        The name of the variable's child to obtain data breakpoint information for.
        If `variablesReference` isn't specified, this can be an expression, or an address if `asAddress` is also true.
       */,
    val frameIdOpt: Option[Z] /* When `name` is an expression, evaluate it in the scope of this stack frame. If not specified, the expression is evaluated in the global scope. When `variablesReference` is specified, this property has no effect. */,
    val bytesOpt: Option[Z]
      /*
        If specified, a debug adapter should return information for the range of memory extending `bytes` number of bytes from the address or variable specified by `name`. Breakpoints set using the resulting data ID should pause on data access anywhere within that range.
        Clients may set this property only if the `supportsDataBreakpointBytes` capability is true.
       */,
    val asAddressOpt: Option[B]
      /*
        If `true`, the `name` is a memory address and the debugger should interpret it as a decimal value, or hex value if it is prefixed with `0x`.
        Clients may set this property only if the `supportsDataBreakpointBytes`
        capability is true.
       */,
    val modeOpt: Option[String] /* The mode of the desired breakpoint. If defined, this must be one of the `breakpointModes` the debug adapter advertised in its `Capabilities`. */
  ) extends `.Node` {
    @strictpure def variablesReference: Z = variablesReferenceOpt.get
    @strictpure def frameId: Z = frameIdOpt.get
    @strictpure def bytes: Z = bytesOpt.get
    @strictpure def asAddress: B = asAddressOpt.get
    @strictpure def mode: String = modeOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (variablesReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("variablesReference", None()), AST.Int(variablesReference, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("name", None()), AST.Str(name, None()))
      if (frameIdOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("frameId", None()), AST.Int(frameId, None()))
      }
      if (bytesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("bytes", None()), AST.Int(bytes, None()))
      }
      if (asAddressOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("asAddress", None()), AST.Bool(asAddress, None()))
      }
      if (modeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("mode", None()), AST.Str(mode, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toDataBreakpointInfoArguments(ast: AST.Obj): DataBreakpointInfoArguments = {
    val map = ast.asMap
    val variablesReferenceOpt = map.getIntValueOpt("variablesReference")
    val name = map.getStr("name").value
    val frameIdOpt = map.getIntValueOpt("frameId")
    val bytesOpt = map.getIntValueOpt("bytes")
    val asAddressOpt = map.getBoolValueOpt("asAddress")
    val modeOpt = map.getStrValueOpt("mode")
    return DataBreakpointInfoArguments(variablesReferenceOpt, name, frameIdOpt, bytesOpt, asAddressOpt, modeOpt)
  }

  @pure def mkDataBreakpointInfoArguments(
    name: String
      /*
        The name of the variable's child to obtain data breakpoint information for.
        If `variablesReference` isn't specified, this can be an expression, or an address if `asAddress` is also true.
       */
  ): DataBreakpointInfoArguments = {
    return DataBreakpointInfoArguments(None(), name, None(), None(), None(), None())
  }

  @pure def fromISZDataBreakpointAccessType(seq: ISZ[DataBreakpointAccessType]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @datatype class DataBreakpointInfoResponseBody(
    val dataId: `.Node`
      /*
        An identifier for the data on which a data breakpoint can be registered with the `setDataBreakpoints` request or null if no data breakpoint is available. If a `variablesReference` or `frameId` is passed, the `dataId` is valid in the current suspended state, otherwise it's valid indefinitely. See 'Lifetime of Object References' in the Overview section for details. Breakpoints set using the `dataId` in the `setDataBreakpoints` request may outlive the lifetime of the associated `dataId`.
        Has to be any of { string, null }
       */,
    val description: String /* UI string that describes on what data the breakpoint is set on or why a data breakpoint is not available. */,
    val accessTypesOpt: Option[ISZ[DataBreakpointAccessType]] /* Attribute lists the available access types for a potential data breakpoint. A UI client could surface this information. */,
    val canPersistOpt: Option[B] /* Attribute indicates that a potential data breakpoint could be persisted across sessions. */
  ) extends `.Node` {
    @strictpure def accessTypes: ISZ[DataBreakpointAccessType] /* Attribute lists the available access types for a potential data breakpoint. A UI client could surface this information. */ = accessTypesOpt.get
    @strictpure def canPersist: B /* Attribute indicates that a potential data breakpoint could be persisted across sessions. */ = canPersistOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("dataId", None()), dataId.toAST)
      kvs = kvs :+ AST.KeyValue(AST.Str("description", None()), AST.Str(description, None()))
      if (accessTypesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("accessTypes", None()), fromISZDataBreakpointAccessType(accessTypes))
      }
      if (canPersistOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("canPersist", None()), AST.Bool(canPersist, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZDataBreakpointAccessType(ast: AST.Arr): ISZ[DataBreakpointAccessType] = {
    var r = ISZ[DataBreakpointAccessType]()
    for (v <- ast.values) {
      r = r :+ toDataBreakpointAccessType(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toDataBreakpointInfoResponseBody(ast: AST.Obj): DataBreakpointInfoResponseBody = {
    val map = ast.asMap
    val dataId = `.Node`.Raw(map.get("dataId"))
    val description = map.getStr("description").value
    val accessTypesOpt = map.getArrOpt("accessTypes").map((o: AST.Arr) => toISZDataBreakpointAccessType(o))
    val canPersistOpt = map.getBoolValueOpt("canPersist")
    return DataBreakpointInfoResponseBody(dataId, description, accessTypesOpt, canPersistOpt)
  }

  @pure def mkDataBreakpointInfoResponseBody(
    dataId: `.Node`
      /*
        An identifier for the data on which a data breakpoint can be registered with the `setDataBreakpoints` request or null if no data breakpoint is available. If a `variablesReference` or `frameId` is passed, the `dataId` is valid in the current suspended state, otherwise it's valid indefinitely. See 'Lifetime of Object References' in the Overview section for details. Breakpoints set using the `dataId` in the `setDataBreakpoints` request may outlive the lifetime of the associated `dataId`.
        Has to be any of { string, null }
       */,
    description: String /* UI string that describes on what data the breakpoint is set on or why a data breakpoint is not available. */
  ): DataBreakpointInfoResponseBody = {
    return DataBreakpointInfoResponseBody(dataId, description, None(), None())
  }

  /* Response to `dataBreakpointInfo` request. */
  @datatype class DataBreakpointInfoResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: DataBreakpointInfoResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toDataBreakpointInfoResponse(ast: AST.Obj): DataBreakpointInfoResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toDataBreakpointInfoResponseBody(map.getObj("body"))
    return DataBreakpointInfoResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkDataBreakpointInfoResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: DataBreakpointInfoResponseBody
  ): DataBreakpointInfoResponse = {
    return DataBreakpointInfoResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    Replaces all existing data breakpoints with new data breakpoints.
    To clear all data breakpoints, specify an empty array.
    When a data breakpoint is hit, a `stopped` event (with reason `data breakpoint`) is generated.
    Clients should only call this request if the corresponding capability `supportsDataBreakpoints` is true.
   */
  @datatype class SetDataBreakpointsRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          setDataBreakpoints
        }
       */,
    val arguments: SetDataBreakpointsArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "setDataBreakpoints")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetDataBreakpointsRequest(ast: AST.Obj): SetDataBreakpointsRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "setDataBreakpoints")
    val arguments = toSetDataBreakpointsArguments(map.getObj("arguments"))
    return SetDataBreakpointsRequest(seq, `type`, command, arguments)
  }

  @pure def mkSetDataBreakpointsRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: SetDataBreakpointsArguments
  ): SetDataBreakpointsRequest = {
    return SetDataBreakpointsRequest(seq, "request", "setDataBreakpoints", arguments)
  }

  @pure def fromISZDataBreakpoint(seq: ISZ[DataBreakpoint]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  /* Arguments for `setDataBreakpoints` request. */
  @datatype class SetDataBreakpointsArguments(
    val breakpoints: ISZ[DataBreakpoint] /* The contents of this array replaces all existing data breakpoints. An empty array clears all data breakpoints. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("breakpoints", None()), fromISZDataBreakpoint(breakpoints))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZDataBreakpoint(ast: AST.Arr): ISZ[DataBreakpoint] = {
    var r = ISZ[DataBreakpoint]()
    for (v <- ast.values) {
      r = r :+ toDataBreakpoint(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toSetDataBreakpointsArguments(ast: AST.Obj): SetDataBreakpointsArguments = {
    val map = ast.asMap
    val breakpoints = toISZDataBreakpoint(map.getArr("breakpoints"))
    return SetDataBreakpointsArguments(breakpoints)
  }

  @pure def mkSetDataBreakpointsArguments(
    breakpoints: ISZ[DataBreakpoint] /* The contents of this array replaces all existing data breakpoints. An empty array clears all data breakpoints. */
  ): SetDataBreakpointsArguments = {
    return SetDataBreakpointsArguments(breakpoints)
  }

  @datatype class SetDataBreakpointsResponseBody(
    val breakpoints: ISZ[Breakpoint] /* Information about the data breakpoints. The array elements correspond to the elements of the input argument `breakpoints` array. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("breakpoints", None()), fromISZBreakpoint(breakpoints))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetDataBreakpointsResponseBody(ast: AST.Obj): SetDataBreakpointsResponseBody = {
    val map = ast.asMap
    val breakpoints = toISZBreakpoint(map.getArr("breakpoints"))
    return SetDataBreakpointsResponseBody(breakpoints)
  }

  @pure def mkSetDataBreakpointsResponseBody(
    breakpoints: ISZ[Breakpoint] /* Information about the data breakpoints. The array elements correspond to the elements of the input argument `breakpoints` array. */
  ): SetDataBreakpointsResponseBody = {
    return SetDataBreakpointsResponseBody(breakpoints)
  }

  /*
    Response to `setDataBreakpoints` request.
    Returned is information about each breakpoint created by this request.
   */
  @datatype class SetDataBreakpointsResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: SetDataBreakpointsResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetDataBreakpointsResponse(ast: AST.Obj): SetDataBreakpointsResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toSetDataBreakpointsResponseBody(map.getObj("body"))
    return SetDataBreakpointsResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkSetDataBreakpointsResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: SetDataBreakpointsResponseBody
  ): SetDataBreakpointsResponse = {
    return SetDataBreakpointsResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    Replaces all existing instruction breakpoints. Typically, instruction breakpoints would be set from a disassembly window. 
    To clear all instruction breakpoints, specify an empty array.
    When an instruction breakpoint is hit, a `stopped` event (with reason `instruction breakpoint`) is generated.
    Clients should only call this request if the corresponding capability `supportsInstructionBreakpoints` is true.
   */
  @datatype class SetInstructionBreakpointsRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          setInstructionBreakpoints
        }
       */,
    val arguments: SetInstructionBreakpointsArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "setInstructionBreakpoints")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetInstructionBreakpointsRequest(ast: AST.Obj): SetInstructionBreakpointsRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "setInstructionBreakpoints")
    val arguments = toSetInstructionBreakpointsArguments(map.getObj("arguments"))
    return SetInstructionBreakpointsRequest(seq, `type`, command, arguments)
  }

  @pure def mkSetInstructionBreakpointsRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: SetInstructionBreakpointsArguments
  ): SetInstructionBreakpointsRequest = {
    return SetInstructionBreakpointsRequest(seq, "request", "setInstructionBreakpoints", arguments)
  }

  @pure def fromISZInstructionBreakpoint(seq: ISZ[InstructionBreakpoint]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  /* Arguments for `setInstructionBreakpoints` request */
  @datatype class SetInstructionBreakpointsArguments(
    val breakpoints: ISZ[InstructionBreakpoint] /* The instruction references of the breakpoints */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("breakpoints", None()), fromISZInstructionBreakpoint(breakpoints))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZInstructionBreakpoint(ast: AST.Arr): ISZ[InstructionBreakpoint] = {
    var r = ISZ[InstructionBreakpoint]()
    for (v <- ast.values) {
      r = r :+ toInstructionBreakpoint(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toSetInstructionBreakpointsArguments(ast: AST.Obj): SetInstructionBreakpointsArguments = {
    val map = ast.asMap
    val breakpoints = toISZInstructionBreakpoint(map.getArr("breakpoints"))
    return SetInstructionBreakpointsArguments(breakpoints)
  }

  @pure def mkSetInstructionBreakpointsArguments(
    breakpoints: ISZ[InstructionBreakpoint] /* The instruction references of the breakpoints */
  ): SetInstructionBreakpointsArguments = {
    return SetInstructionBreakpointsArguments(breakpoints)
  }

  @datatype class SetInstructionBreakpointsResponseBody(
    val breakpoints: ISZ[Breakpoint] /* Information about the breakpoints. The array elements correspond to the elements of the `breakpoints` array. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("breakpoints", None()), fromISZBreakpoint(breakpoints))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetInstructionBreakpointsResponseBody(ast: AST.Obj): SetInstructionBreakpointsResponseBody = {
    val map = ast.asMap
    val breakpoints = toISZBreakpoint(map.getArr("breakpoints"))
    return SetInstructionBreakpointsResponseBody(breakpoints)
  }

  @pure def mkSetInstructionBreakpointsResponseBody(
    breakpoints: ISZ[Breakpoint] /* Information about the breakpoints. The array elements correspond to the elements of the `breakpoints` array. */
  ): SetInstructionBreakpointsResponseBody = {
    return SetInstructionBreakpointsResponseBody(breakpoints)
  }

  /* Response to `setInstructionBreakpoints` request */
  @datatype class SetInstructionBreakpointsResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: SetInstructionBreakpointsResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetInstructionBreakpointsResponse(ast: AST.Obj): SetInstructionBreakpointsResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toSetInstructionBreakpointsResponseBody(map.getObj("body"))
    return SetInstructionBreakpointsResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkSetInstructionBreakpointsResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: SetInstructionBreakpointsResponseBody
  ): SetInstructionBreakpointsResponse = {
    return SetInstructionBreakpointsResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /* The request resumes execution of all threads. If the debug adapter supports single thread execution (see capability `supportsSingleThreadExecutionRequests`), setting the `singleThread` argument to true resumes only the specified thread. If not all threads were resumed, the `allThreadsContinued` attribute of the response should be set to false. */
  @datatype class ContinueRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          continue
        }
       */,
    val arguments: ContinueArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "continue")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toContinueRequest(ast: AST.Obj): ContinueRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "continue")
    val arguments = toContinueArguments(map.getObj("arguments"))
    return ContinueRequest(seq, `type`, command, arguments)
  }

  @pure def mkContinueRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: ContinueArguments
  ): ContinueRequest = {
    return ContinueRequest(seq, "request", "continue", arguments)
  }

  /* Arguments for `continue` request. */
  @datatype class ContinueArguments(
    val threadId: Z /* Specifies the active thread. If the debug adapter supports single thread execution (see `supportsSingleThreadExecutionRequests`) and the argument `singleThread` is true, only the thread with this ID is resumed. */,
    val singleThreadOpt: Option[B] /* If this flag is true, execution is resumed only for the thread with given `threadId`. */
  ) extends `.Node` {
    @strictpure def singleThread: B = singleThreadOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("threadId", None()), AST.Int(threadId, None()))
      if (singleThreadOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("singleThread", None()), AST.Bool(singleThread, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toContinueArguments(ast: AST.Obj): ContinueArguments = {
    val map = ast.asMap
    val threadId = map.getInt("threadId").value
    val singleThreadOpt = map.getBoolValueOpt("singleThread")
    return ContinueArguments(threadId, singleThreadOpt)
  }

  @pure def mkContinueArguments(
    threadId: Z /* Specifies the active thread. If the debug adapter supports single thread execution (see `supportsSingleThreadExecutionRequests`) and the argument `singleThread` is true, only the thread with this ID is resumed. */
  ): ContinueArguments = {
    return ContinueArguments(threadId, None())
  }

  @datatype class ContinueResponseBody(
    val allThreadsContinuedOpt: Option[B] /* The value true (or a missing property) signals to the client that all threads have been resumed. The value false indicates that not all threads were resumed. */
  ) extends `.Node` {
    @strictpure def allThreadsContinued: B /* The value true (or a missing property) signals to the client that all threads have been resumed. The value false indicates that not all threads were resumed. */ = allThreadsContinuedOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (allThreadsContinuedOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("allThreadsContinued", None()), AST.Bool(allThreadsContinued, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toContinueResponseBody(ast: AST.Obj): ContinueResponseBody = {
    val map = ast.asMap
    val allThreadsContinuedOpt = map.getBoolValueOpt("allThreadsContinued")
    return ContinueResponseBody(allThreadsContinuedOpt)
  }

  @pure def mkContinueResponseBody(
  ): ContinueResponseBody = {
    return ContinueResponseBody(None())
  }

  /* Response to `continue` request. */
  @datatype class ContinueResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: ContinueResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toContinueResponse(ast: AST.Obj): ContinueResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toContinueResponseBody(map.getObj("body"))
    return ContinueResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkContinueResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: ContinueResponseBody
  ): ContinueResponse = {
    return ContinueResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    The request executes one step (in the given granularity) for the specified thread and allows all other threads to run freely by resuming them.
    If the debug adapter supports single thread execution (see capability `supportsSingleThreadExecutionRequests`), setting the `singleThread` argument to true prevents other suspended threads from resuming.
    The debug adapter first sends the response and then a `stopped` event (with reason `step`) after the step has completed.
   */
  @datatype class NextRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          next
        }
       */,
    val arguments: NextArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "next")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toNextRequest(ast: AST.Obj): NextRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "next")
    val arguments = toNextArguments(map.getObj("arguments"))
    return NextRequest(seq, `type`, command, arguments)
  }

  @pure def mkNextRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: NextArguments
  ): NextRequest = {
    return NextRequest(seq, "request", "next", arguments)
  }

  /* Arguments for `next` request. */
  @datatype class NextArguments(
    val threadId: Z /* Specifies the thread for which to resume execution for one step (of the given granularity). */,
    val singleThreadOpt: Option[B] /* If this flag is true, all other suspended threads are not resumed. */,
    val granularityOpt: Option[SteppingGranularity] /* Stepping granularity. If no granularity is specified, a granularity of `statement` is assumed. */
  ) extends `.Node` {
    @strictpure def singleThread: B = singleThreadOpt.get
    @strictpure def granularity: SteppingGranularity = granularityOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("threadId", None()), AST.Int(threadId, None()))
      if (singleThreadOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("singleThread", None()), AST.Bool(singleThread, None()))
      }
      if (granularityOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("granularity", None()), granularity.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toNextArguments(ast: AST.Obj): NextArguments = {
    val map = ast.asMap
    val threadId = map.getInt("threadId").value
    val singleThreadOpt = map.getBoolValueOpt("singleThread")
    val granularityOpt = map.getObjOpt("granularity").map((o: AST.Obj) => toSteppingGranularity(o))
    return NextArguments(threadId, singleThreadOpt, granularityOpt)
  }

  @pure def mkNextArguments(
    threadId: Z /* Specifies the thread for which to resume execution for one step (of the given granularity). */
  ): NextArguments = {
    return NextArguments(threadId, None(), None())
  }

  /* Response to `next` request. This is just an acknowledgement, so no body field is required. */
  @datatype class NextResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toNextResponse(ast: AST.Obj): NextResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return NextResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkNextResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): NextResponse = {
    return NextResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    The request resumes the given thread to step into a function/method and allows all other threads to run freely by resuming them.
    If the debug adapter supports single thread execution (see capability `supportsSingleThreadExecutionRequests`), setting the `singleThread` argument to true prevents other suspended threads from resuming.
    If the request cannot step into a target, `stepIn` behaves like the `next` request.
    The debug adapter first sends the response and then a `stopped` event (with reason `step`) after the step has completed.
    If there are multiple function/method calls (or other targets) on the source line,
    the argument `targetId` can be used to control into which target the `stepIn` should occur.
    The list of possible targets for a given source line can be retrieved via the `stepInTargets` request.
   */
  @datatype class StepInRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          stepIn
        }
       */,
    val arguments: StepInArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "stepIn")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStepInRequest(ast: AST.Obj): StepInRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "stepIn")
    val arguments = toStepInArguments(map.getObj("arguments"))
    return StepInRequest(seq, `type`, command, arguments)
  }

  @pure def mkStepInRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: StepInArguments
  ): StepInRequest = {
    return StepInRequest(seq, "request", "stepIn", arguments)
  }

  /* Arguments for `stepIn` request. */
  @datatype class StepInArguments(
    val threadId: Z /* Specifies the thread for which to resume execution for one step-into (of the given granularity). */,
    val singleThreadOpt: Option[B] /* If this flag is true, all other suspended threads are not resumed. */,
    val targetIdOpt: Option[Z] /* Id of the target to step into. */,
    val granularityOpt: Option[SteppingGranularity] /* Stepping granularity. If no granularity is specified, a granularity of `statement` is assumed. */
  ) extends `.Node` {
    @strictpure def singleThread: B = singleThreadOpt.get
    @strictpure def targetId: Z = targetIdOpt.get
    @strictpure def granularity: SteppingGranularity = granularityOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("threadId", None()), AST.Int(threadId, None()))
      if (singleThreadOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("singleThread", None()), AST.Bool(singleThread, None()))
      }
      if (targetIdOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("targetId", None()), AST.Int(targetId, None()))
      }
      if (granularityOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("granularity", None()), granularity.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStepInArguments(ast: AST.Obj): StepInArguments = {
    val map = ast.asMap
    val threadId = map.getInt("threadId").value
    val singleThreadOpt = map.getBoolValueOpt("singleThread")
    val targetIdOpt = map.getIntValueOpt("targetId")
    val granularityOpt = map.getObjOpt("granularity").map((o: AST.Obj) => toSteppingGranularity(o))
    return StepInArguments(threadId, singleThreadOpt, targetIdOpt, granularityOpt)
  }

  @pure def mkStepInArguments(
    threadId: Z /* Specifies the thread for which to resume execution for one step-into (of the given granularity). */
  ): StepInArguments = {
    return StepInArguments(threadId, None(), None(), None())
  }

  /* Response to `stepIn` request. This is just an acknowledgement, so no body field is required. */
  @datatype class StepInResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStepInResponse(ast: AST.Obj): StepInResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return StepInResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkStepInResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): StepInResponse = {
    return StepInResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    The request resumes the given thread to step out (return) from a function/method and allows all other threads to run freely by resuming them.
    If the debug adapter supports single thread execution (see capability `supportsSingleThreadExecutionRequests`), setting the `singleThread` argument to true prevents other suspended threads from resuming.
    The debug adapter first sends the response and then a `stopped` event (with reason `step`) after the step has completed.
   */
  @datatype class StepOutRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          stepOut
        }
       */,
    val arguments: StepOutArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "stepOut")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStepOutRequest(ast: AST.Obj): StepOutRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "stepOut")
    val arguments = toStepOutArguments(map.getObj("arguments"))
    return StepOutRequest(seq, `type`, command, arguments)
  }

  @pure def mkStepOutRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: StepOutArguments
  ): StepOutRequest = {
    return StepOutRequest(seq, "request", "stepOut", arguments)
  }

  /* Arguments for `stepOut` request. */
  @datatype class StepOutArguments(
    val threadId: Z /* Specifies the thread for which to resume execution for one step-out (of the given granularity). */,
    val singleThreadOpt: Option[B] /* If this flag is true, all other suspended threads are not resumed. */,
    val granularityOpt: Option[SteppingGranularity] /* Stepping granularity. If no granularity is specified, a granularity of `statement` is assumed. */
  ) extends `.Node` {
    @strictpure def singleThread: B = singleThreadOpt.get
    @strictpure def granularity: SteppingGranularity = granularityOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("threadId", None()), AST.Int(threadId, None()))
      if (singleThreadOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("singleThread", None()), AST.Bool(singleThread, None()))
      }
      if (granularityOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("granularity", None()), granularity.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStepOutArguments(ast: AST.Obj): StepOutArguments = {
    val map = ast.asMap
    val threadId = map.getInt("threadId").value
    val singleThreadOpt = map.getBoolValueOpt("singleThread")
    val granularityOpt = map.getObjOpt("granularity").map((o: AST.Obj) => toSteppingGranularity(o))
    return StepOutArguments(threadId, singleThreadOpt, granularityOpt)
  }

  @pure def mkStepOutArguments(
    threadId: Z /* Specifies the thread for which to resume execution for one step-out (of the given granularity). */
  ): StepOutArguments = {
    return StepOutArguments(threadId, None(), None())
  }

  /* Response to `stepOut` request. This is just an acknowledgement, so no body field is required. */
  @datatype class StepOutResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStepOutResponse(ast: AST.Obj): StepOutResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return StepOutResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkStepOutResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): StepOutResponse = {
    return StepOutResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    The request executes one backward step (in the given granularity) for the specified thread and allows all other threads to run backward freely by resuming them.
    If the debug adapter supports single thread execution (see capability `supportsSingleThreadExecutionRequests`), setting the `singleThread` argument to true prevents other suspended threads from resuming.
    The debug adapter first sends the response and then a `stopped` event (with reason `step`) after the step has completed.
    Clients should only call this request if the corresponding capability `supportsStepBack` is true.
   */
  @datatype class StepBackRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          stepBack
        }
       */,
    val arguments: StepBackArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "stepBack")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStepBackRequest(ast: AST.Obj): StepBackRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "stepBack")
    val arguments = toStepBackArguments(map.getObj("arguments"))
    return StepBackRequest(seq, `type`, command, arguments)
  }

  @pure def mkStepBackRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: StepBackArguments
  ): StepBackRequest = {
    return StepBackRequest(seq, "request", "stepBack", arguments)
  }

  /* Arguments for `stepBack` request. */
  @datatype class StepBackArguments(
    val threadId: Z /* Specifies the thread for which to resume execution for one step backwards (of the given granularity). */,
    val singleThreadOpt: Option[B] /* If this flag is true, all other suspended threads are not resumed. */,
    val granularityOpt: Option[SteppingGranularity] /* Stepping granularity to step. If no granularity is specified, a granularity of `statement` is assumed. */
  ) extends `.Node` {
    @strictpure def singleThread: B = singleThreadOpt.get
    @strictpure def granularity: SteppingGranularity = granularityOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("threadId", None()), AST.Int(threadId, None()))
      if (singleThreadOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("singleThread", None()), AST.Bool(singleThread, None()))
      }
      if (granularityOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("granularity", None()), granularity.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStepBackArguments(ast: AST.Obj): StepBackArguments = {
    val map = ast.asMap
    val threadId = map.getInt("threadId").value
    val singleThreadOpt = map.getBoolValueOpt("singleThread")
    val granularityOpt = map.getObjOpt("granularity").map((o: AST.Obj) => toSteppingGranularity(o))
    return StepBackArguments(threadId, singleThreadOpt, granularityOpt)
  }

  @pure def mkStepBackArguments(
    threadId: Z /* Specifies the thread for which to resume execution for one step backwards (of the given granularity). */
  ): StepBackArguments = {
    return StepBackArguments(threadId, None(), None())
  }

  /* Response to `stepBack` request. This is just an acknowledgement, so no body field is required. */
  @datatype class StepBackResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStepBackResponse(ast: AST.Obj): StepBackResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return StepBackResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkStepBackResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): StepBackResponse = {
    return StepBackResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    The request resumes backward execution of all threads. If the debug adapter supports single thread execution (see capability `supportsSingleThreadExecutionRequests`), setting the `singleThread` argument to true resumes only the specified thread. If not all threads were resumed, the `allThreadsContinued` attribute of the response should be set to false.
    Clients should only call this request if the corresponding capability `supportsStepBack` is true.
   */
  @datatype class ReverseContinueRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          reverseContinue
        }
       */,
    val arguments: ReverseContinueArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "reverseContinue")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toReverseContinueRequest(ast: AST.Obj): ReverseContinueRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "reverseContinue")
    val arguments = toReverseContinueArguments(map.getObj("arguments"))
    return ReverseContinueRequest(seq, `type`, command, arguments)
  }

  @pure def mkReverseContinueRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: ReverseContinueArguments
  ): ReverseContinueRequest = {
    return ReverseContinueRequest(seq, "request", "reverseContinue", arguments)
  }

  /* Arguments for `reverseContinue` request. */
  @datatype class ReverseContinueArguments(
    val threadId: Z /* Specifies the active thread. If the debug adapter supports single thread execution (see `supportsSingleThreadExecutionRequests`) and the `singleThread` argument is true, only the thread with this ID is resumed. */,
    val singleThreadOpt: Option[B] /* If this flag is true, backward execution is resumed only for the thread with given `threadId`. */
  ) extends `.Node` {
    @strictpure def singleThread: B = singleThreadOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("threadId", None()), AST.Int(threadId, None()))
      if (singleThreadOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("singleThread", None()), AST.Bool(singleThread, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toReverseContinueArguments(ast: AST.Obj): ReverseContinueArguments = {
    val map = ast.asMap
    val threadId = map.getInt("threadId").value
    val singleThreadOpt = map.getBoolValueOpt("singleThread")
    return ReverseContinueArguments(threadId, singleThreadOpt)
  }

  @pure def mkReverseContinueArguments(
    threadId: Z /* Specifies the active thread. If the debug adapter supports single thread execution (see `supportsSingleThreadExecutionRequests`) and the `singleThread` argument is true, only the thread with this ID is resumed. */
  ): ReverseContinueArguments = {
    return ReverseContinueArguments(threadId, None())
  }

  /* Response to `reverseContinue` request. This is just an acknowledgement, so no body field is required. */
  @datatype class ReverseContinueResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toReverseContinueResponse(ast: AST.Obj): ReverseContinueResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return ReverseContinueResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkReverseContinueResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): ReverseContinueResponse = {
    return ReverseContinueResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    The request restarts execution of the specified stack frame.
    The debug adapter first sends the response and then a `stopped` event (with reason `restart`) after the restart has completed.
    Clients should only call this request if the corresponding capability `supportsRestartFrame` is true.
   */
  @datatype class RestartFrameRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          restartFrame
        }
       */,
    val arguments: RestartFrameArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "restartFrame")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toRestartFrameRequest(ast: AST.Obj): RestartFrameRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "restartFrame")
    val arguments = toRestartFrameArguments(map.getObj("arguments"))
    return RestartFrameRequest(seq, `type`, command, arguments)
  }

  @pure def mkRestartFrameRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: RestartFrameArguments
  ): RestartFrameRequest = {
    return RestartFrameRequest(seq, "request", "restartFrame", arguments)
  }

  /* Arguments for `restartFrame` request. */
  @datatype class RestartFrameArguments(
    val frameId: Z /* Restart the stack frame identified by `frameId`. The `frameId` must have been obtained in the current suspended state. See 'Lifetime of Object References' in the Overview section for details. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("frameId", None()), AST.Int(frameId, None()))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toRestartFrameArguments(ast: AST.Obj): RestartFrameArguments = {
    val map = ast.asMap
    val frameId = map.getInt("frameId").value
    return RestartFrameArguments(frameId)
  }

  @pure def mkRestartFrameArguments(
    frameId: Z /* Restart the stack frame identified by `frameId`. The `frameId` must have been obtained in the current suspended state. See 'Lifetime of Object References' in the Overview section for details. */
  ): RestartFrameArguments = {
    return RestartFrameArguments(frameId)
  }

  /* Response to `restartFrame` request. This is just an acknowledgement, so no body field is required. */
  @datatype class RestartFrameResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toRestartFrameResponse(ast: AST.Obj): RestartFrameResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return RestartFrameResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkRestartFrameResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): RestartFrameResponse = {
    return RestartFrameResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    The request sets the location where the debuggee will continue to run.
    This makes it possible to skip the execution of code or to execute code again.
    The code between the current location and the goto target is not executed but skipped.
    The debug adapter first sends the response and then a `stopped` event with reason `goto`.
    Clients should only call this request if the corresponding capability `supportsGotoTargetsRequest` is true (because only then goto targets exist that can be passed as arguments).
   */
  @datatype class GotoRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          goto
        }
       */,
    val arguments: GotoArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "goto")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toGotoRequest(ast: AST.Obj): GotoRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "goto")
    val arguments = toGotoArguments(map.getObj("arguments"))
    return GotoRequest(seq, `type`, command, arguments)
  }

  @pure def mkGotoRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: GotoArguments
  ): GotoRequest = {
    return GotoRequest(seq, "request", "goto", arguments)
  }

  /* Arguments for `goto` request. */
  @datatype class GotoArguments(
    val threadId: Z /* Set the goto target for this thread. */,
    val targetId: Z /* The location where the debuggee will continue to run. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("threadId", None()), AST.Int(threadId, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("targetId", None()), AST.Int(targetId, None()))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toGotoArguments(ast: AST.Obj): GotoArguments = {
    val map = ast.asMap
    val threadId = map.getInt("threadId").value
    val targetId = map.getInt("targetId").value
    return GotoArguments(threadId, targetId)
  }

  @pure def mkGotoArguments(
    threadId: Z /* Set the goto target for this thread. */,
    targetId: Z /* The location where the debuggee will continue to run. */
  ): GotoArguments = {
    return GotoArguments(threadId, targetId)
  }

  /* Response to `goto` request. This is just an acknowledgement, so no body field is required. */
  @datatype class GotoResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toGotoResponse(ast: AST.Obj): GotoResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return GotoResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkGotoResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): GotoResponse = {
    return GotoResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    The request suspends the debuggee.
    The debug adapter first sends the response and then a `stopped` event (with reason `pause`) after the thread has been paused successfully.
   */
  @datatype class PauseRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          pause
        }
       */,
    val arguments: PauseArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "pause")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toPauseRequest(ast: AST.Obj): PauseRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "pause")
    val arguments = toPauseArguments(map.getObj("arguments"))
    return PauseRequest(seq, `type`, command, arguments)
  }

  @pure def mkPauseRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: PauseArguments
  ): PauseRequest = {
    return PauseRequest(seq, "request", "pause", arguments)
  }

  /* Arguments for `pause` request. */
  @datatype class PauseArguments(
    val threadId: Z /* Pause execution for this thread. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("threadId", None()), AST.Int(threadId, None()))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toPauseArguments(ast: AST.Obj): PauseArguments = {
    val map = ast.asMap
    val threadId = map.getInt("threadId").value
    return PauseArguments(threadId)
  }

  @pure def mkPauseArguments(
    threadId: Z /* Pause execution for this thread. */
  ): PauseArguments = {
    return PauseArguments(threadId)
  }

  /* Response to `pause` request. This is just an acknowledgement, so no body field is required. */
  @datatype class PauseResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toPauseResponse(ast: AST.Obj): PauseResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return PauseResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkPauseResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): PauseResponse = {
    return PauseResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    The request returns a stacktrace from the current execution state of a given thread.
    A client can request all stack frames by omitting the startFrame and levels arguments. For performance-conscious clients and if the corresponding capability `supportsDelayedStackTraceLoading` is true, stack frames can be retrieved in a piecemeal way with the `startFrame` and `levels` arguments. The response of the `stackTrace` request may contain a `totalFrames` property that hints at the total number of frames in the stack. If a client needs this total number upfront, it can issue a request for a single (first) frame and depending on the value of `totalFrames` decide how to proceed. In any case a client should be prepared to receive fewer frames than requested, which is an indication that the end of the stack has been reached.
   */
  @datatype class StackTraceRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          stackTrace
        }
       */,
    val arguments: StackTraceArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "stackTrace")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStackTraceRequest(ast: AST.Obj): StackTraceRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "stackTrace")
    val arguments = toStackTraceArguments(map.getObj("arguments"))
    return StackTraceRequest(seq, `type`, command, arguments)
  }

  @pure def mkStackTraceRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: StackTraceArguments
  ): StackTraceRequest = {
    return StackTraceRequest(seq, "request", "stackTrace", arguments)
  }

  /* Arguments for `stackTrace` request. */
  @datatype class StackTraceArguments(
    val threadId: Z /* Retrieve the stacktrace for this thread. */,
    val startFrameOpt: Option[Z] /* The index of the first frame to return; if omitted frames start at 0. */,
    val levelsOpt: Option[Z] /* The maximum number of frames to return. If levels is not specified or 0, all frames are returned. */,
    val formatOpt: Option[StackFrameFormat]
      /*
        Specifies details on how to format the stack frames.
        The attribute is only honored by a debug adapter if the corresponding capability `supportsValueFormattingOptions` is true.
       */
  ) extends `.Node` {
    @strictpure def startFrame: Z = startFrameOpt.get
    @strictpure def levels: Z = levelsOpt.get
    @strictpure def format: StackFrameFormat = formatOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("threadId", None()), AST.Int(threadId, None()))
      if (startFrameOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("startFrame", None()), AST.Int(startFrame, None()))
      }
      if (levelsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("levels", None()), AST.Int(levels, None()))
      }
      if (formatOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("format", None()), format.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStackTraceArguments(ast: AST.Obj): StackTraceArguments = {
    val map = ast.asMap
    val threadId = map.getInt("threadId").value
    val startFrameOpt = map.getIntValueOpt("startFrame")
    val levelsOpt = map.getIntValueOpt("levels")
    val formatOpt = map.getObjOpt("format").map((o: AST.Obj) => toStackFrameFormat(o))
    return StackTraceArguments(threadId, startFrameOpt, levelsOpt, formatOpt)
  }

  @pure def mkStackTraceArguments(
    threadId: Z /* Retrieve the stacktrace for this thread. */
  ): StackTraceArguments = {
    return StackTraceArguments(threadId, None(), None(), None())
  }

  @pure def fromISZStackFrame(seq: ISZ[StackFrame]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @datatype class StackTraceResponseBody(
    val stackFrames: ISZ[StackFrame]
      /*
        The frames of the stack frame. If the array has length zero, there are no stack frames available.
        This means that there is no location information available.
       */,
    val totalFramesOpt: Option[Z] /* The total number of frames available in the stack. If omitted or if `totalFrames` is larger than the available frames, a client is expected to request frames until a request returns less frames than requested (which indicates the end of the stack). Returning monotonically increasing `totalFrames` values for subsequent requests can be used to enforce paging in the client. */
  ) extends `.Node` {
    @strictpure def totalFrames: Z /* The total number of frames available in the stack. If omitted or if `totalFrames` is larger than the available frames, a client is expected to request frames until a request returns less frames than requested (which indicates the end of the stack). Returning monotonically increasing `totalFrames` values for subsequent requests can be used to enforce paging in the client. */ = totalFramesOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("stackFrames", None()), fromISZStackFrame(stackFrames))
      if (totalFramesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("totalFrames", None()), AST.Int(totalFrames, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZStackFrame(ast: AST.Arr): ISZ[StackFrame] = {
    var r = ISZ[StackFrame]()
    for (v <- ast.values) {
      r = r :+ toStackFrame(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toStackTraceResponseBody(ast: AST.Obj): StackTraceResponseBody = {
    val map = ast.asMap
    val stackFrames = toISZStackFrame(map.getArr("stackFrames"))
    val totalFramesOpt = map.getIntValueOpt("totalFrames")
    return StackTraceResponseBody(stackFrames, totalFramesOpt)
  }

  @pure def mkStackTraceResponseBody(
    stackFrames: ISZ[StackFrame]
      /*
        The frames of the stack frame. If the array has length zero, there are no stack frames available.
        This means that there is no location information available.
       */
  ): StackTraceResponseBody = {
    return StackTraceResponseBody(stackFrames, None())
  }

  /* Response to `stackTrace` request. */
  @datatype class StackTraceResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: StackTraceResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStackTraceResponse(ast: AST.Obj): StackTraceResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toStackTraceResponseBody(map.getObj("body"))
    return StackTraceResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkStackTraceResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: StackTraceResponseBody
  ): StackTraceResponse = {
    return StackTraceResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /* The request returns the variable scopes for a given stack frame ID. */
  @datatype class ScopesRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          scopes
        }
       */,
    val arguments: ScopesArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "scopes")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toScopesRequest(ast: AST.Obj): ScopesRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "scopes")
    val arguments = toScopesArguments(map.getObj("arguments"))
    return ScopesRequest(seq, `type`, command, arguments)
  }

  @pure def mkScopesRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: ScopesArguments
  ): ScopesRequest = {
    return ScopesRequest(seq, "request", "scopes", arguments)
  }

  /* Arguments for `scopes` request. */
  @datatype class ScopesArguments(
    val frameId: Z /* Retrieve the scopes for the stack frame identified by `frameId`. The `frameId` must have been obtained in the current suspended state. See 'Lifetime of Object References' in the Overview section for details. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("frameId", None()), AST.Int(frameId, None()))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toScopesArguments(ast: AST.Obj): ScopesArguments = {
    val map = ast.asMap
    val frameId = map.getInt("frameId").value
    return ScopesArguments(frameId)
  }

  @pure def mkScopesArguments(
    frameId: Z /* Retrieve the scopes for the stack frame identified by `frameId`. The `frameId` must have been obtained in the current suspended state. See 'Lifetime of Object References' in the Overview section for details. */
  ): ScopesArguments = {
    return ScopesArguments(frameId)
  }

  @pure def fromISZScope(seq: ISZ[Scope]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @datatype class ScopesResponseBody(
    val scopes: ISZ[Scope] /* The scopes of the stack frame. If the array has length zero, there are no scopes available. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("scopes", None()), fromISZScope(scopes))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZScope(ast: AST.Arr): ISZ[Scope] = {
    var r = ISZ[Scope]()
    for (v <- ast.values) {
      r = r :+ toScope(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toScopesResponseBody(ast: AST.Obj): ScopesResponseBody = {
    val map = ast.asMap
    val scopes = toISZScope(map.getArr("scopes"))
    return ScopesResponseBody(scopes)
  }

  @pure def mkScopesResponseBody(
    scopes: ISZ[Scope] /* The scopes of the stack frame. If the array has length zero, there are no scopes available. */
  ): ScopesResponseBody = {
    return ScopesResponseBody(scopes)
  }

  /* Response to `scopes` request. */
  @datatype class ScopesResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: ScopesResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toScopesResponse(ast: AST.Obj): ScopesResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toScopesResponseBody(map.getObj("body"))
    return ScopesResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkScopesResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: ScopesResponseBody
  ): ScopesResponse = {
    return ScopesResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    Retrieves all child variables for the given variable reference.
    A filter can be used to limit the fetched children to either named or indexed children.
   */
  @datatype class VariablesRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          variables
        }
       */,
    val arguments: VariablesArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "variables")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toVariablesRequest(ast: AST.Obj): VariablesRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "variables")
    val arguments = toVariablesArguments(map.getObj("arguments"))
    return VariablesRequest(seq, `type`, command, arguments)
  }

  @pure def mkVariablesRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: VariablesArguments
  ): VariablesRequest = {
    return VariablesRequest(seq, "request", "variables", arguments)
  }

  /* Arguments for `variables` request. */
  @datatype class VariablesArguments(
    val variablesReference: Z /* The variable for which to retrieve its children. The `variablesReference` must have been obtained in the current suspended state. See 'Lifetime of Object References' in the Overview section for details. */,
    val filterOpt: Option[String]
      /*
        Filter to limit the child variables to either named or indexed. If omitted, both types are fetched.
        Has to be one of {
          indexed,
          named
        }
       */,
    val startOpt: Option[Z]
      /*
        The index of the first variable to return; if omitted children start at 0.
        The attribute is only honored by a debug adapter if the corresponding capability `supportsVariablePaging` is true.
       */,
    val countOpt: Option[Z]
      /*
        The number of variables to return. If count is missing or 0, all variables are returned.
        The attribute is only honored by a debug adapter if the corresponding capability `supportsVariablePaging` is true.
       */,
    val formatOpt: Option[ValueFormat]
      /*
        Specifies details on how to format the Variable values.
        The attribute is only honored by a debug adapter if the corresponding capability `supportsValueFormattingOptions` is true.
       */
  ) extends `.Node` {
    @strictpure def filter: String = filterOpt.get
    @strictpure def start: Z = startOpt.get
    @strictpure def count: Z = countOpt.get
    @strictpure def format: ValueFormat = formatOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("variablesReference", None()), AST.Int(variablesReference, None()))
      assert(filter == "indexed" || filter == "named")
      if (filterOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("filter", None()), AST.Str(filter, None()))
      }
      if (startOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("start", None()), AST.Int(start, None()))
      }
      if (countOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("count", None()), AST.Int(count, None()))
      }
      if (formatOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("format", None()), format.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toVariablesArguments(ast: AST.Obj): VariablesArguments = {
    val map = ast.asMap
    val variablesReference = map.getInt("variablesReference").value
    val filterOpt = map.getStrValueOpt("filter")
    filterOpt match {
      case Some(s) => assert(s == "indexed" || s == "named")
      case _ =>
    }
    val startOpt = map.getIntValueOpt("start")
    val countOpt = map.getIntValueOpt("count")
    val formatOpt = map.getObjOpt("format").map((o: AST.Obj) => toValueFormat(o))
    return VariablesArguments(variablesReference, filterOpt, startOpt, countOpt, formatOpt)
  }

  @pure def mkVariablesArguments(
    variablesReference: Z /* The variable for which to retrieve its children. The `variablesReference` must have been obtained in the current suspended state. See 'Lifetime of Object References' in the Overview section for details. */
  ): VariablesArguments = {
    return VariablesArguments(variablesReference, None(), None(), None(), None())
  }

  @pure def fromISZVariable(seq: ISZ[Variable]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @datatype class VariablesResponseBody(
    val variables: ISZ[Variable] /* All (or a range) of variables for the given variable reference. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("variables", None()), fromISZVariable(variables))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZVariable(ast: AST.Arr): ISZ[Variable] = {
    var r = ISZ[Variable]()
    for (v <- ast.values) {
      r = r :+ toVariable(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toVariablesResponseBody(ast: AST.Obj): VariablesResponseBody = {
    val map = ast.asMap
    val variables = toISZVariable(map.getArr("variables"))
    return VariablesResponseBody(variables)
  }

  @pure def mkVariablesResponseBody(
    variables: ISZ[Variable] /* All (or a range) of variables for the given variable reference. */
  ): VariablesResponseBody = {
    return VariablesResponseBody(variables)
  }

  /* Response to `variables` request. */
  @datatype class VariablesResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: VariablesResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toVariablesResponse(ast: AST.Obj): VariablesResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toVariablesResponseBody(map.getObj("body"))
    return VariablesResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkVariablesResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: VariablesResponseBody
  ): VariablesResponse = {
    return VariablesResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    Set the variable with the given name in the variable container to a new value. Clients should only call this request if the corresponding capability `supportsSetVariable` is true.
    If a debug adapter implements both `setVariable` and `setExpression`, a client will only use `setExpression` if the variable has an `evaluateName` property.
   */
  @datatype class SetVariableRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          setVariable
        }
       */,
    val arguments: SetVariableArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "setVariable")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetVariableRequest(ast: AST.Obj): SetVariableRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "setVariable")
    val arguments = toSetVariableArguments(map.getObj("arguments"))
    return SetVariableRequest(seq, `type`, command, arguments)
  }

  @pure def mkSetVariableRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: SetVariableArguments
  ): SetVariableRequest = {
    return SetVariableRequest(seq, "request", "setVariable", arguments)
  }

  /* Arguments for `setVariable` request. */
  @datatype class SetVariableArguments(
    val variablesReference: Z /* The reference of the variable container. The `variablesReference` must have been obtained in the current suspended state. See 'Lifetime of Object References' in the Overview section for details. */,
    val name: String /* The name of the variable in the container. */,
    val value: String /* The value of the variable. */,
    val formatOpt: Option[ValueFormat] /* Specifies details on how to format the response value. */
  ) extends `.Node` {
    @strictpure def format: ValueFormat = formatOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("variablesReference", None()), AST.Int(variablesReference, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("name", None()), AST.Str(name, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("value", None()), AST.Str(value, None()))
      if (formatOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("format", None()), format.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetVariableArguments(ast: AST.Obj): SetVariableArguments = {
    val map = ast.asMap
    val variablesReference = map.getInt("variablesReference").value
    val name = map.getStr("name").value
    val value = map.getStr("value").value
    val formatOpt = map.getObjOpt("format").map((o: AST.Obj) => toValueFormat(o))
    return SetVariableArguments(variablesReference, name, value, formatOpt)
  }

  @pure def mkSetVariableArguments(
    variablesReference: Z /* The reference of the variable container. The `variablesReference` must have been obtained in the current suspended state. See 'Lifetime of Object References' in the Overview section for details. */,
    name: String /* The name of the variable in the container. */,
    value: String /* The value of the variable. */
  ): SetVariableArguments = {
    return SetVariableArguments(variablesReference, name, value, None())
  }

  @datatype class SetVariableResponseBody(
    val value: String /* The new value of the variable. */,
    val typeOpt: Option[String] /* The type of the new value. Typically shown in the UI when hovering over the value. */,
    val variablesReferenceOpt: Option[Z]
      /*
        If `variablesReference` is > 0, the new value is structured and its children can be retrieved by passing `variablesReference` to the `variables` request as long as execution remains suspended. See 'Lifetime of Object References' in the Overview section for details.
        If this property is included in the response, any `variablesReference` previously associated with the updated variable, and those of its children, are no longer valid.
       */,
    val namedVariablesOpt: Option[Z]
      /*
        The number of named child variables.
        The client can use this information to present the variables in a paged UI and fetch them in chunks.
        The value should be less than or equal to 2147483647 (2^31-1).
       */,
    val indexedVariablesOpt: Option[Z]
      /*
        The number of indexed child variables.
        The client can use this information to present the variables in a paged UI and fetch them in chunks.
        The value should be less than or equal to 2147483647 (2^31-1).
       */,
    val memoryReferenceOpt: Option[String]
      /*
        A memory reference to a location appropriate for this result.
        For pointer type eval results, this is generally a reference to the memory address contained in the pointer.
        This attribute may be returned by a debug adapter if corresponding capability `supportsMemoryReferences` is true.
       */,
    val valueLocationReferenceOpt: Option[Z]
      /*
        A reference that allows the client to request the location where the new value is declared. For example, if the new value is function pointer, the adapter may be able to look up the function's location. This should be present only if the adapter is likely to be able to resolve the location.
        This reference shares the same lifetime as the `variablesReference`. See 'Lifetime of Object References' in the Overview section for details.
       */
  ) extends `.Node` {
    @strictpure def `type`: String /* The type of the new value. Typically shown in the UI when hovering over the value. */ = typeOpt.get
    @strictpure def variablesReference: Z
      /*
        If `variablesReference` is > 0, the new value is structured and its children can be retrieved by passing `variablesReference` to the `variables` request as long as execution remains suspended. See 'Lifetime of Object References' in the Overview section for details.
        If this property is included in the response, any `variablesReference` previously associated with the updated variable, and those of its children, are no longer valid.
       */ = variablesReferenceOpt.get
    @strictpure def namedVariables: Z
      /*
        The number of named child variables.
        The client can use this information to present the variables in a paged UI and fetch them in chunks.
        The value should be less than or equal to 2147483647 (2^31-1).
       */ = namedVariablesOpt.get
    @strictpure def indexedVariables: Z
      /*
        The number of indexed child variables.
        The client can use this information to present the variables in a paged UI and fetch them in chunks.
        The value should be less than or equal to 2147483647 (2^31-1).
       */ = indexedVariablesOpt.get
    @strictpure def memoryReference: String
      /*
        A memory reference to a location appropriate for this result.
        For pointer type eval results, this is generally a reference to the memory address contained in the pointer.
        This attribute may be returned by a debug adapter if corresponding capability `supportsMemoryReferences` is true.
       */ = memoryReferenceOpt.get
    @strictpure def valueLocationReference: Z
      /*
        A reference that allows the client to request the location where the new value is declared. For example, if the new value is function pointer, the adapter may be able to look up the function's location. This should be present only if the adapter is likely to be able to resolve the location.
        This reference shares the same lifetime as the `variablesReference`. See 'Lifetime of Object References' in the Overview section for details.
       */ = valueLocationReferenceOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("value", None()), AST.Str(value, None()))
      if (typeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      }
      if (variablesReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("variablesReference", None()), AST.Int(variablesReference, None()))
      }
      if (namedVariablesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("namedVariables", None()), AST.Int(namedVariables, None()))
      }
      if (indexedVariablesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("indexedVariables", None()), AST.Int(indexedVariables, None()))
      }
      if (memoryReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("memoryReference", None()), AST.Str(memoryReference, None()))
      }
      if (valueLocationReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("valueLocationReference", None()), AST.Int(valueLocationReference, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetVariableResponseBody(ast: AST.Obj): SetVariableResponseBody = {
    val map = ast.asMap
    val value = map.getStr("value").value
    val typeOpt = map.getStrValueOpt("type")
    val variablesReferenceOpt = map.getIntValueOpt("variablesReference")
    val namedVariablesOpt = map.getIntValueOpt("namedVariables")
    val indexedVariablesOpt = map.getIntValueOpt("indexedVariables")
    val memoryReferenceOpt = map.getStrValueOpt("memoryReference")
    val valueLocationReferenceOpt = map.getIntValueOpt("valueLocationReference")
    return SetVariableResponseBody(value, typeOpt, variablesReferenceOpt, namedVariablesOpt, indexedVariablesOpt, memoryReferenceOpt, valueLocationReferenceOpt)
  }

  @pure def mkSetVariableResponseBody(
    value: String /* The new value of the variable. */
  ): SetVariableResponseBody = {
    return SetVariableResponseBody(value, None(), None(), None(), None(), None(), None())
  }

  /* Response to `setVariable` request. */
  @datatype class SetVariableResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: SetVariableResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetVariableResponse(ast: AST.Obj): SetVariableResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toSetVariableResponseBody(map.getObj("body"))
    return SetVariableResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkSetVariableResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: SetVariableResponseBody
  ): SetVariableResponse = {
    return SetVariableResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /* The request retrieves the source code for a given source reference. */
  @datatype class SourceRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          source
        }
       */,
    val arguments: SourceArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "source")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSourceRequest(ast: AST.Obj): SourceRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "source")
    val arguments = toSourceArguments(map.getObj("arguments"))
    return SourceRequest(seq, `type`, command, arguments)
  }

  @pure def mkSourceRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: SourceArguments
  ): SourceRequest = {
    return SourceRequest(seq, "request", "source", arguments)
  }

  /* Arguments for `source` request. */
  @datatype class SourceArguments(
    val sourceOpt: Option[Source] /* Specifies the source content to load. Either `source.path` or `source.sourceReference` must be specified. */,
    val sourceReference: Z
      /*
        The reference to the source. This is the same as `source.sourceReference`.
        This is provided for backward compatibility since old clients do not understand the `source` attribute.
       */
  ) extends `.Node` {
    @strictpure def source: Source = sourceOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (sourceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("source", None()), source.toAST)
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("sourceReference", None()), AST.Int(sourceReference, None()))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSourceArguments(ast: AST.Obj): SourceArguments = {
    val map = ast.asMap
    val sourceOpt = map.getObjOpt("source").map((o: AST.Obj) => toSource(o))
    val sourceReference = map.getInt("sourceReference").value
    return SourceArguments(sourceOpt, sourceReference)
  }

  @pure def mkSourceArguments(
    sourceReference: Z
      /*
        The reference to the source. This is the same as `source.sourceReference`.
        This is provided for backward compatibility since old clients do not understand the `source` attribute.
       */
  ): SourceArguments = {
    return SourceArguments(None(), sourceReference)
  }

  @datatype class SourceResponseBody(
    val content: String /* Content of the source reference. */,
    val mimeTypeOpt: Option[String] /* Content type (MIME type) of the source. */
  ) extends `.Node` {
    @strictpure def mimeType: String /* Content type (MIME type) of the source. */ = mimeTypeOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("content", None()), AST.Str(content, None()))
      if (mimeTypeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("mimeType", None()), AST.Str(mimeType, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSourceResponseBody(ast: AST.Obj): SourceResponseBody = {
    val map = ast.asMap
    val content = map.getStr("content").value
    val mimeTypeOpt = map.getStrValueOpt("mimeType")
    return SourceResponseBody(content, mimeTypeOpt)
  }

  @pure def mkSourceResponseBody(
    content: String /* Content of the source reference. */
  ): SourceResponseBody = {
    return SourceResponseBody(content, None())
  }

  /* Response to `source` request. */
  @datatype class SourceResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: SourceResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSourceResponse(ast: AST.Obj): SourceResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toSourceResponseBody(map.getObj("body"))
    return SourceResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkSourceResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: SourceResponseBody
  ): SourceResponse = {
    return SourceResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /* The request retrieves a list of all threads. */
  @datatype class ThreadsRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          threads
        }
       */,
    val argumentsOpt: Option[`.Node`]
      /*
        Object containing arguments for the command.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Request {
    @strictpure def arguments: `.Node` = argumentsOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "threads")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      if (argumentsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toThreadsRequest(ast: AST.Obj): ThreadsRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "threads")
    val argumentsOpt = map.getOpt("arguments").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return ThreadsRequest(seq, `type`, command, argumentsOpt)
  }

  @pure def mkThreadsRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */
  ): ThreadsRequest = {
    return ThreadsRequest(seq, "request", "threads", None())
  }

  @pure def fromISZThread(seq: ISZ[Thread]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @datatype class ThreadsResponseBody(
    val threads: ISZ[Thread] /* All threads. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("threads", None()), fromISZThread(threads))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZThread(ast: AST.Arr): ISZ[Thread] = {
    var r = ISZ[Thread]()
    for (v <- ast.values) {
      r = r :+ toThread(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toThreadsResponseBody(ast: AST.Obj): ThreadsResponseBody = {
    val map = ast.asMap
    val threads = toISZThread(map.getArr("threads"))
    return ThreadsResponseBody(threads)
  }

  @pure def mkThreadsResponseBody(
    threads: ISZ[Thread] /* All threads. */
  ): ThreadsResponseBody = {
    return ThreadsResponseBody(threads)
  }

  /* Response to `threads` request. */
  @datatype class ThreadsResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: ThreadsResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toThreadsResponse(ast: AST.Obj): ThreadsResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toThreadsResponseBody(map.getObj("body"))
    return ThreadsResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkThreadsResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: ThreadsResponseBody
  ): ThreadsResponse = {
    return ThreadsResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    The request terminates the threads with the given ids.
    Clients should only call this request if the corresponding capability `supportsTerminateThreadsRequest` is true.
   */
  @datatype class TerminateThreadsRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          terminateThreads
        }
       */,
    val arguments: TerminateThreadsArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "terminateThreads")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toTerminateThreadsRequest(ast: AST.Obj): TerminateThreadsRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "terminateThreads")
    val arguments = toTerminateThreadsArguments(map.getObj("arguments"))
    return TerminateThreadsRequest(seq, `type`, command, arguments)
  }

  @pure def mkTerminateThreadsRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: TerminateThreadsArguments
  ): TerminateThreadsRequest = {
    return TerminateThreadsRequest(seq, "request", "terminateThreads", arguments)
  }

  /* Arguments for `terminateThreads` request. */
  @datatype class TerminateThreadsArguments(
    val threadIdsOpt: Option[ISZ[Z]] /* Ids of threads to be terminated. */
  ) extends `.Node` {
    @strictpure def threadIds: ISZ[Z] = threadIdsOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (threadIdsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("threadIds", None()), fromISZZ(threadIds))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toTerminateThreadsArguments(ast: AST.Obj): TerminateThreadsArguments = {
    val map = ast.asMap
    val threadIdsOpt = map.getArrOpt("threadIds").map((o: AST.Arr) => toISZZ(o))
    return TerminateThreadsArguments(threadIdsOpt)
  }

  @pure def mkTerminateThreadsArguments(
  ): TerminateThreadsArguments = {
    return TerminateThreadsArguments(None())
  }

  /* Response to `terminateThreads` request. This is just an acknowledgement, no body field is required. */
  @datatype class TerminateThreadsResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[`.Node`]
      /*
        Contains request result if success is true and error details if success is false.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: `.Node` = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toTerminateThreadsResponse(ast: AST.Obj): TerminateThreadsResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getOpt("body").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    return TerminateThreadsResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkTerminateThreadsResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): TerminateThreadsResponse = {
    return TerminateThreadsResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    Modules can be retrieved from the debug adapter with this request which can either return all modules or a range of modules to support paging.
    Clients should only call this request if the corresponding capability `supportsModulesRequest` is true.
   */
  @datatype class ModulesRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          modules
        }
       */,
    val arguments: ModulesArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "modules")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toModulesRequest(ast: AST.Obj): ModulesRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "modules")
    val arguments = toModulesArguments(map.getObj("arguments"))
    return ModulesRequest(seq, `type`, command, arguments)
  }

  @pure def mkModulesRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: ModulesArguments
  ): ModulesRequest = {
    return ModulesRequest(seq, "request", "modules", arguments)
  }

  /* Arguments for `modules` request. */
  @datatype class ModulesArguments(
    val startModuleOpt: Option[Z] /* The index of the first module to return; if omitted modules start at 0. */,
    val moduleCountOpt: Option[Z] /* The number of modules to return. If `moduleCount` is not specified or 0, all modules are returned. */
  ) extends `.Node` {
    @strictpure def startModule: Z = startModuleOpt.get
    @strictpure def moduleCount: Z = moduleCountOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (startModuleOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("startModule", None()), AST.Int(startModule, None()))
      }
      if (moduleCountOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("moduleCount", None()), AST.Int(moduleCount, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toModulesArguments(ast: AST.Obj): ModulesArguments = {
    val map = ast.asMap
    val startModuleOpt = map.getIntValueOpt("startModule")
    val moduleCountOpt = map.getIntValueOpt("moduleCount")
    return ModulesArguments(startModuleOpt, moduleCountOpt)
  }

  @pure def mkModulesArguments(
  ): ModulesArguments = {
    return ModulesArguments(None(), None())
  }

  @pure def fromISZModule(seq: ISZ[Module]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @datatype class ModulesResponseBody(
    val modules: ISZ[Module] /* All modules or range of modules. */,
    val totalModulesOpt: Option[Z] /* The total number of modules available. */
  ) extends `.Node` {
    @strictpure def totalModules: Z /* The total number of modules available. */ = totalModulesOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("modules", None()), fromISZModule(modules))
      if (totalModulesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("totalModules", None()), AST.Int(totalModules, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZModule(ast: AST.Arr): ISZ[Module] = {
    var r = ISZ[Module]()
    for (v <- ast.values) {
      r = r :+ toModule(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toModulesResponseBody(ast: AST.Obj): ModulesResponseBody = {
    val map = ast.asMap
    val modules = toISZModule(map.getArr("modules"))
    val totalModulesOpt = map.getIntValueOpt("totalModules")
    return ModulesResponseBody(modules, totalModulesOpt)
  }

  @pure def mkModulesResponseBody(
    modules: ISZ[Module] /* All modules or range of modules. */
  ): ModulesResponseBody = {
    return ModulesResponseBody(modules, None())
  }

  /* Response to `modules` request. */
  @datatype class ModulesResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: ModulesResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toModulesResponse(ast: AST.Obj): ModulesResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toModulesResponseBody(map.getObj("body"))
    return ModulesResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkModulesResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: ModulesResponseBody
  ): ModulesResponse = {
    return ModulesResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    Retrieves the set of all sources currently loaded by the debugged process.
    Clients should only call this request if the corresponding capability `supportsLoadedSourcesRequest` is true.
   */
  @datatype class LoadedSourcesRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          loadedSources
        }
       */,
    val argumentsOpt: Option[LoadedSourcesArguments]
  ) extends Request {
    @strictpure def arguments: LoadedSourcesArguments = argumentsOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "loadedSources")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      if (argumentsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toLoadedSourcesRequest(ast: AST.Obj): LoadedSourcesRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "loadedSources")
    val argumentsOpt = map.getObjOpt("arguments").map((o: AST.Obj) => toLoadedSourcesArguments(o))
    return LoadedSourcesRequest(seq, `type`, command, argumentsOpt)
  }

  @pure def mkLoadedSourcesRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */
  ): LoadedSourcesRequest = {
    return LoadedSourcesRequest(seq, "request", "loadedSources", None())
  }

  /* Arguments for `loadedSources` request. */
  @datatype class LoadedSourcesArguments(
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      return AST.Obj(kvs, None())
    }
  }

  @pure def toLoadedSourcesArguments(ast: AST.Obj): LoadedSourcesArguments = {
    val map = ast.asMap
    return LoadedSourcesArguments()
  }

  @pure def mkLoadedSourcesArguments(
  ): LoadedSourcesArguments = {
    return LoadedSourcesArguments()
  }

  @pure def fromISZSource(seq: ISZ[Source]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @datatype class LoadedSourcesResponseBody(
    val sources: ISZ[Source] /* Set of loaded sources. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("sources", None()), fromISZSource(sources))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZSource(ast: AST.Arr): ISZ[Source] = {
    var r = ISZ[Source]()
    for (v <- ast.values) {
      r = r :+ toSource(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toLoadedSourcesResponseBody(ast: AST.Obj): LoadedSourcesResponseBody = {
    val map = ast.asMap
    val sources = toISZSource(map.getArr("sources"))
    return LoadedSourcesResponseBody(sources)
  }

  @pure def mkLoadedSourcesResponseBody(
    sources: ISZ[Source] /* Set of loaded sources. */
  ): LoadedSourcesResponseBody = {
    return LoadedSourcesResponseBody(sources)
  }

  /* Response to `loadedSources` request. */
  @datatype class LoadedSourcesResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: LoadedSourcesResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toLoadedSourcesResponse(ast: AST.Obj): LoadedSourcesResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toLoadedSourcesResponseBody(map.getObj("body"))
    return LoadedSourcesResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkLoadedSourcesResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: LoadedSourcesResponseBody
  ): LoadedSourcesResponse = {
    return LoadedSourcesResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    Evaluates the given expression in the context of a stack frame.
    The expression has access to any variables and arguments that are in scope.
   */
  @datatype class EvaluateRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          evaluate
        }
       */,
    val arguments: EvaluateArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "evaluate")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toEvaluateRequest(ast: AST.Obj): EvaluateRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "evaluate")
    val arguments = toEvaluateArguments(map.getObj("arguments"))
    return EvaluateRequest(seq, `type`, command, arguments)
  }

  @pure def mkEvaluateRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: EvaluateArguments
  ): EvaluateRequest = {
    return EvaluateRequest(seq, "request", "evaluate", arguments)
  }

  /* Arguments for `evaluate` request. */
  @datatype class EvaluateArguments(
    val expression: String /* The expression to evaluate. */,
    val frameIdOpt: Option[Z] /* Evaluate the expression in the scope of this stack frame. If not specified, the expression is evaluated in the global scope. */,
    val lineOpt: Option[Z] /* The contextual line where the expression should be evaluated. In the 'hover' context, this should be set to the start of the expression being hovered. */,
    val columnOpt: Option[Z]
      /*
        The contextual column where the expression should be evaluated. This may be provided if `line` is also provided.
        It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based.
       */,
    val sourceOpt: Option[Source] /* The contextual source in which the `line` is found. This must be provided if `line` is provided. */,
    val contextOpt: Option[String]
      /*
        The context in which the evaluate request is used.
        Has to be one of {
          watch /* evaluate is called from a watch view context. */,
          repl /* evaluate is called from a REPL context. */,
          hover /*
            evaluate is called to generate the debug hover contents.
            This value should only be used if the corresponding capability `supportsEvaluateForHovers` is true.
           */,
          clipboard /*
            evaluate is called to generate clipboard contents.
            This value should only be used if the corresponding capability `supportsClipboardContext` is true.
           */,
          variables /* evaluate is called from a variables view context. */
        }
       */,
    val formatOpt: Option[ValueFormat]
      /*
        Specifies details on how to format the result.
        The attribute is only honored by a debug adapter if the corresponding capability `supportsValueFormattingOptions` is true.
       */
  ) extends `.Node` {
    @strictpure def frameId: Z = frameIdOpt.get
    @strictpure def line: Z = lineOpt.get
    @strictpure def column: Z = columnOpt.get
    @strictpure def source: Source = sourceOpt.get
    @strictpure def context: String = contextOpt.get
    @strictpure def format: ValueFormat = formatOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("expression", None()), AST.Str(expression, None()))
      if (frameIdOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("frameId", None()), AST.Int(frameId, None()))
      }
      if (lineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("line", None()), AST.Int(line, None()))
      }
      if (columnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("column", None()), AST.Int(column, None()))
      }
      if (sourceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("source", None()), source.toAST)
      }
      assert(context == "watch" || context == "repl" || context == "hover" || context == "clipboard" || context == "variables")
      if (contextOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("context", None()), AST.Str(context, None()))
      }
      if (formatOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("format", None()), format.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toEvaluateArguments(ast: AST.Obj): EvaluateArguments = {
    val map = ast.asMap
    val expression = map.getStr("expression").value
    val frameIdOpt = map.getIntValueOpt("frameId")
    val lineOpt = map.getIntValueOpt("line")
    val columnOpt = map.getIntValueOpt("column")
    val sourceOpt = map.getObjOpt("source").map((o: AST.Obj) => toSource(o))
    val contextOpt = map.getStrValueOpt("context")
    contextOpt match {
      case Some(s) => assert(s == "watch" || s == "repl" || s == "hover" || s == "clipboard" || s == "variables")
      case _ =>
    }
    val formatOpt = map.getObjOpt("format").map((o: AST.Obj) => toValueFormat(o))
    return EvaluateArguments(expression, frameIdOpt, lineOpt, columnOpt, sourceOpt, contextOpt, formatOpt)
  }

  @pure def mkEvaluateArguments(
    expression: String /* The expression to evaluate. */
  ): EvaluateArguments = {
    return EvaluateArguments(expression, None(), None(), None(), None(), None(), None())
  }

  @datatype class EvaluateResponseBody(
    val result: String /* The result of the evaluate request. */,
    val typeOpt: Option[String]
      /*
        The type of the evaluate result.
        This attribute should only be returned by a debug adapter if the corresponding capability `supportsVariableType` is true.
       */,
    val presentationHintOpt: Option[VariablePresentationHint] /* Properties of an evaluate result that can be used to determine how to render the result in the UI. */,
    val variablesReference: Z /* If `variablesReference` is > 0, the evaluate result is structured and its children can be retrieved by passing `variablesReference` to the `variables` request as long as execution remains suspended. See 'Lifetime of Object References' in the Overview section for details. */,
    val namedVariablesOpt: Option[Z]
      /*
        The number of named child variables.
        The client can use this information to present the variables in a paged UI and fetch them in chunks.
        The value should be less than or equal to 2147483647 (2^31-1).
       */,
    val indexedVariablesOpt: Option[Z]
      /*
        The number of indexed child variables.
        The client can use this information to present the variables in a paged UI and fetch them in chunks.
        The value should be less than or equal to 2147483647 (2^31-1).
       */,
    val memoryReferenceOpt: Option[String]
      /*
        A memory reference to a location appropriate for this result.
        For pointer type eval results, this is generally a reference to the memory address contained in the pointer.
        This attribute may be returned by a debug adapter if corresponding capability `supportsMemoryReferences` is true.
       */,
    val valueLocationReferenceOpt: Option[Z]
      /*
        A reference that allows the client to request the location where the returned value is declared. For example, if a function pointer is returned, the adapter may be able to look up the function's location. This should be present only if the adapter is likely to be able to resolve the location.
        This reference shares the same lifetime as the `variablesReference`. See 'Lifetime of Object References' in the Overview section for details.
       */
  ) extends `.Node` {
    @strictpure def `type`: String
      /*
        The type of the evaluate result.
        This attribute should only be returned by a debug adapter if the corresponding capability `supportsVariableType` is true.
       */ = typeOpt.get
    @strictpure def presentationHint: VariablePresentationHint /* Properties of an evaluate result that can be used to determine how to render the result in the UI. */ = presentationHintOpt.get
    @strictpure def namedVariables: Z
      /*
        The number of named child variables.
        The client can use this information to present the variables in a paged UI and fetch them in chunks.
        The value should be less than or equal to 2147483647 (2^31-1).
       */ = namedVariablesOpt.get
    @strictpure def indexedVariables: Z
      /*
        The number of indexed child variables.
        The client can use this information to present the variables in a paged UI and fetch them in chunks.
        The value should be less than or equal to 2147483647 (2^31-1).
       */ = indexedVariablesOpt.get
    @strictpure def memoryReference: String
      /*
        A memory reference to a location appropriate for this result.
        For pointer type eval results, this is generally a reference to the memory address contained in the pointer.
        This attribute may be returned by a debug adapter if corresponding capability `supportsMemoryReferences` is true.
       */ = memoryReferenceOpt.get
    @strictpure def valueLocationReference: Z
      /*
        A reference that allows the client to request the location where the returned value is declared. For example, if a function pointer is returned, the adapter may be able to look up the function's location. This should be present only if the adapter is likely to be able to resolve the location.
        This reference shares the same lifetime as the `variablesReference`. See 'Lifetime of Object References' in the Overview section for details.
       */ = valueLocationReferenceOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("result", None()), AST.Str(result, None()))
      if (typeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      }
      if (presentationHintOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("presentationHint", None()), presentationHint.toAST)
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("variablesReference", None()), AST.Int(variablesReference, None()))
      if (namedVariablesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("namedVariables", None()), AST.Int(namedVariables, None()))
      }
      if (indexedVariablesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("indexedVariables", None()), AST.Int(indexedVariables, None()))
      }
      if (memoryReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("memoryReference", None()), AST.Str(memoryReference, None()))
      }
      if (valueLocationReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("valueLocationReference", None()), AST.Int(valueLocationReference, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toEvaluateResponseBody(ast: AST.Obj): EvaluateResponseBody = {
    val map = ast.asMap
    val result = map.getStr("result").value
    val typeOpt = map.getStrValueOpt("type")
    val presentationHintOpt = map.getObjOpt("presentationHint").map((o: AST.Obj) => toVariablePresentationHint(o))
    val variablesReference = map.getInt("variablesReference").value
    val namedVariablesOpt = map.getIntValueOpt("namedVariables")
    val indexedVariablesOpt = map.getIntValueOpt("indexedVariables")
    val memoryReferenceOpt = map.getStrValueOpt("memoryReference")
    val valueLocationReferenceOpt = map.getIntValueOpt("valueLocationReference")
    return EvaluateResponseBody(result, typeOpt, presentationHintOpt, variablesReference, namedVariablesOpt, indexedVariablesOpt, memoryReferenceOpt, valueLocationReferenceOpt)
  }

  @pure def mkEvaluateResponseBody(
    result: String /* The result of the evaluate request. */,
    variablesReference: Z /* If `variablesReference` is > 0, the evaluate result is structured and its children can be retrieved by passing `variablesReference` to the `variables` request as long as execution remains suspended. See 'Lifetime of Object References' in the Overview section for details. */
  ): EvaluateResponseBody = {
    return EvaluateResponseBody(result, None(), None(), variablesReference, None(), None(), None(), None())
  }

  /* Response to `evaluate` request. */
  @datatype class EvaluateResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: EvaluateResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toEvaluateResponse(ast: AST.Obj): EvaluateResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toEvaluateResponseBody(map.getObj("body"))
    return EvaluateResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkEvaluateResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: EvaluateResponseBody
  ): EvaluateResponse = {
    return EvaluateResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    Evaluates the given `value` expression and assigns it to the `expression` which must be a modifiable l-value.
    The expressions have access to any variables and arguments that are in scope of the specified frame.
    Clients should only call this request if the corresponding capability `supportsSetExpression` is true.
    If a debug adapter implements both `setExpression` and `setVariable`, a client uses `setExpression` if the variable has an `evaluateName` property.
   */
  @datatype class SetExpressionRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          setExpression
        }
       */,
    val arguments: SetExpressionArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "setExpression")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetExpressionRequest(ast: AST.Obj): SetExpressionRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "setExpression")
    val arguments = toSetExpressionArguments(map.getObj("arguments"))
    return SetExpressionRequest(seq, `type`, command, arguments)
  }

  @pure def mkSetExpressionRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: SetExpressionArguments
  ): SetExpressionRequest = {
    return SetExpressionRequest(seq, "request", "setExpression", arguments)
  }

  /* Arguments for `setExpression` request. */
  @datatype class SetExpressionArguments(
    val expression: String /* The l-value expression to assign to. */,
    val value: String /* The value expression to assign to the l-value expression. */,
    val frameIdOpt: Option[Z] /* Evaluate the expressions in the scope of this stack frame. If not specified, the expressions are evaluated in the global scope. */,
    val formatOpt: Option[ValueFormat] /* Specifies how the resulting value should be formatted. */
  ) extends `.Node` {
    @strictpure def frameId: Z = frameIdOpt.get
    @strictpure def format: ValueFormat = formatOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("expression", None()), AST.Str(expression, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("value", None()), AST.Str(value, None()))
      if (frameIdOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("frameId", None()), AST.Int(frameId, None()))
      }
      if (formatOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("format", None()), format.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetExpressionArguments(ast: AST.Obj): SetExpressionArguments = {
    val map = ast.asMap
    val expression = map.getStr("expression").value
    val value = map.getStr("value").value
    val frameIdOpt = map.getIntValueOpt("frameId")
    val formatOpt = map.getObjOpt("format").map((o: AST.Obj) => toValueFormat(o))
    return SetExpressionArguments(expression, value, frameIdOpt, formatOpt)
  }

  @pure def mkSetExpressionArguments(
    expression: String /* The l-value expression to assign to. */,
    value: String /* The value expression to assign to the l-value expression. */
  ): SetExpressionArguments = {
    return SetExpressionArguments(expression, value, None(), None())
  }

  @datatype class SetExpressionResponseBody(
    val value: String /* The new value of the expression. */,
    val typeOpt: Option[String]
      /*
        The type of the value.
        This attribute should only be returned by a debug adapter if the corresponding capability `supportsVariableType` is true.
       */,
    val presentationHintOpt: Option[VariablePresentationHint] /* Properties of a value that can be used to determine how to render the result in the UI. */,
    val variablesReferenceOpt: Option[Z] /* If `variablesReference` is > 0, the evaluate result is structured and its children can be retrieved by passing `variablesReference` to the `variables` request as long as execution remains suspended. See 'Lifetime of Object References' in the Overview section for details. */,
    val namedVariablesOpt: Option[Z]
      /*
        The number of named child variables.
        The client can use this information to present the variables in a paged UI and fetch them in chunks.
        The value should be less than or equal to 2147483647 (2^31-1).
       */,
    val indexedVariablesOpt: Option[Z]
      /*
        The number of indexed child variables.
        The client can use this information to present the variables in a paged UI and fetch them in chunks.
        The value should be less than or equal to 2147483647 (2^31-1).
       */,
    val memoryReferenceOpt: Option[String]
      /*
        A memory reference to a location appropriate for this result.
        For pointer type eval results, this is generally a reference to the memory address contained in the pointer.
        This attribute may be returned by a debug adapter if corresponding capability `supportsMemoryReferences` is true.
       */,
    val valueLocationReferenceOpt: Option[Z]
      /*
        A reference that allows the client to request the location where the new value is declared. For example, if the new value is function pointer, the adapter may be able to look up the function's location. This should be present only if the adapter is likely to be able to resolve the location.
        This reference shares the same lifetime as the `variablesReference`. See 'Lifetime of Object References' in the Overview section for details.
       */
  ) extends `.Node` {
    @strictpure def `type`: String
      /*
        The type of the value.
        This attribute should only be returned by a debug adapter if the corresponding capability `supportsVariableType` is true.
       */ = typeOpt.get
    @strictpure def presentationHint: VariablePresentationHint /* Properties of a value that can be used to determine how to render the result in the UI. */ = presentationHintOpt.get
    @strictpure def variablesReference: Z /* If `variablesReference` is > 0, the evaluate result is structured and its children can be retrieved by passing `variablesReference` to the `variables` request as long as execution remains suspended. See 'Lifetime of Object References' in the Overview section for details. */ = variablesReferenceOpt.get
    @strictpure def namedVariables: Z
      /*
        The number of named child variables.
        The client can use this information to present the variables in a paged UI and fetch them in chunks.
        The value should be less than or equal to 2147483647 (2^31-1).
       */ = namedVariablesOpt.get
    @strictpure def indexedVariables: Z
      /*
        The number of indexed child variables.
        The client can use this information to present the variables in a paged UI and fetch them in chunks.
        The value should be less than or equal to 2147483647 (2^31-1).
       */ = indexedVariablesOpt.get
    @strictpure def memoryReference: String
      /*
        A memory reference to a location appropriate for this result.
        For pointer type eval results, this is generally a reference to the memory address contained in the pointer.
        This attribute may be returned by a debug adapter if corresponding capability `supportsMemoryReferences` is true.
       */ = memoryReferenceOpt.get
    @strictpure def valueLocationReference: Z
      /*
        A reference that allows the client to request the location where the new value is declared. For example, if the new value is function pointer, the adapter may be able to look up the function's location. This should be present only if the adapter is likely to be able to resolve the location.
        This reference shares the same lifetime as the `variablesReference`. See 'Lifetime of Object References' in the Overview section for details.
       */ = valueLocationReferenceOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("value", None()), AST.Str(value, None()))
      if (typeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      }
      if (presentationHintOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("presentationHint", None()), presentationHint.toAST)
      }
      if (variablesReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("variablesReference", None()), AST.Int(variablesReference, None()))
      }
      if (namedVariablesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("namedVariables", None()), AST.Int(namedVariables, None()))
      }
      if (indexedVariablesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("indexedVariables", None()), AST.Int(indexedVariables, None()))
      }
      if (memoryReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("memoryReference", None()), AST.Str(memoryReference, None()))
      }
      if (valueLocationReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("valueLocationReference", None()), AST.Int(valueLocationReference, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetExpressionResponseBody(ast: AST.Obj): SetExpressionResponseBody = {
    val map = ast.asMap
    val value = map.getStr("value").value
    val typeOpt = map.getStrValueOpt("type")
    val presentationHintOpt = map.getObjOpt("presentationHint").map((o: AST.Obj) => toVariablePresentationHint(o))
    val variablesReferenceOpt = map.getIntValueOpt("variablesReference")
    val namedVariablesOpt = map.getIntValueOpt("namedVariables")
    val indexedVariablesOpt = map.getIntValueOpt("indexedVariables")
    val memoryReferenceOpt = map.getStrValueOpt("memoryReference")
    val valueLocationReferenceOpt = map.getIntValueOpt("valueLocationReference")
    return SetExpressionResponseBody(value, typeOpt, presentationHintOpt, variablesReferenceOpt, namedVariablesOpt, indexedVariablesOpt, memoryReferenceOpt, valueLocationReferenceOpt)
  }

  @pure def mkSetExpressionResponseBody(
    value: String /* The new value of the expression. */
  ): SetExpressionResponseBody = {
    return SetExpressionResponseBody(value, None(), None(), None(), None(), None(), None(), None())
  }

  /* Response to `setExpression` request. */
  @datatype class SetExpressionResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: SetExpressionResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSetExpressionResponse(ast: AST.Obj): SetExpressionResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toSetExpressionResponseBody(map.getObj("body"))
    return SetExpressionResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkSetExpressionResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: SetExpressionResponseBody
  ): SetExpressionResponse = {
    return SetExpressionResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    This request retrieves the possible step-in targets for the specified stack frame.
    These targets can be used in the `stepIn` request.
    Clients should only call this request if the corresponding capability `supportsStepInTargetsRequest` is true.
   */
  @datatype class StepInTargetsRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          stepInTargets
        }
       */,
    val arguments: StepInTargetsArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "stepInTargets")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStepInTargetsRequest(ast: AST.Obj): StepInTargetsRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "stepInTargets")
    val arguments = toStepInTargetsArguments(map.getObj("arguments"))
    return StepInTargetsRequest(seq, `type`, command, arguments)
  }

  @pure def mkStepInTargetsRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: StepInTargetsArguments
  ): StepInTargetsRequest = {
    return StepInTargetsRequest(seq, "request", "stepInTargets", arguments)
  }

  /* Arguments for `stepInTargets` request. */
  @datatype class StepInTargetsArguments(
    val frameId: Z /* The stack frame for which to retrieve the possible step-in targets. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("frameId", None()), AST.Int(frameId, None()))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStepInTargetsArguments(ast: AST.Obj): StepInTargetsArguments = {
    val map = ast.asMap
    val frameId = map.getInt("frameId").value
    return StepInTargetsArguments(frameId)
  }

  @pure def mkStepInTargetsArguments(
    frameId: Z /* The stack frame for which to retrieve the possible step-in targets. */
  ): StepInTargetsArguments = {
    return StepInTargetsArguments(frameId)
  }

  @pure def fromISZStepInTarget(seq: ISZ[StepInTarget]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @datatype class StepInTargetsResponseBody(
    val targets: ISZ[StepInTarget] /* The possible step-in targets of the specified source location. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("targets", None()), fromISZStepInTarget(targets))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZStepInTarget(ast: AST.Arr): ISZ[StepInTarget] = {
    var r = ISZ[StepInTarget]()
    for (v <- ast.values) {
      r = r :+ toStepInTarget(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toStepInTargetsResponseBody(ast: AST.Obj): StepInTargetsResponseBody = {
    val map = ast.asMap
    val targets = toISZStepInTarget(map.getArr("targets"))
    return StepInTargetsResponseBody(targets)
  }

  @pure def mkStepInTargetsResponseBody(
    targets: ISZ[StepInTarget] /* The possible step-in targets of the specified source location. */
  ): StepInTargetsResponseBody = {
    return StepInTargetsResponseBody(targets)
  }

  /* Response to `stepInTargets` request. */
  @datatype class StepInTargetsResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: StepInTargetsResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStepInTargetsResponse(ast: AST.Obj): StepInTargetsResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toStepInTargetsResponseBody(map.getObj("body"))
    return StepInTargetsResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkStepInTargetsResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: StepInTargetsResponseBody
  ): StepInTargetsResponse = {
    return StepInTargetsResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    This request retrieves the possible goto targets for the specified source location.
    These targets can be used in the `goto` request.
    Clients should only call this request if the corresponding capability `supportsGotoTargetsRequest` is true.
   */
  @datatype class GotoTargetsRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          gotoTargets
        }
       */,
    val arguments: GotoTargetsArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "gotoTargets")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toGotoTargetsRequest(ast: AST.Obj): GotoTargetsRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "gotoTargets")
    val arguments = toGotoTargetsArguments(map.getObj("arguments"))
    return GotoTargetsRequest(seq, `type`, command, arguments)
  }

  @pure def mkGotoTargetsRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: GotoTargetsArguments
  ): GotoTargetsRequest = {
    return GotoTargetsRequest(seq, "request", "gotoTargets", arguments)
  }

  /* Arguments for `gotoTargets` request. */
  @datatype class GotoTargetsArguments(
    val source: Source /* The source location for which the goto targets are determined. */,
    val line: Z /* The line location for which the goto targets are determined. */,
    val columnOpt: Option[Z] /* The position within `line` for which the goto targets are determined. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. */
  ) extends `.Node` {
    @strictpure def column: Z = columnOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("source", None()), source.toAST)
      kvs = kvs :+ AST.KeyValue(AST.Str("line", None()), AST.Int(line, None()))
      if (columnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("column", None()), AST.Int(column, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toGotoTargetsArguments(ast: AST.Obj): GotoTargetsArguments = {
    val map = ast.asMap
    val source = toSource(map.getObj("source"))
    val line = map.getInt("line").value
    val columnOpt = map.getIntValueOpt("column")
    return GotoTargetsArguments(source, line, columnOpt)
  }

  @pure def mkGotoTargetsArguments(
    source: Source /* The source location for which the goto targets are determined. */,
    line: Z /* The line location for which the goto targets are determined. */
  ): GotoTargetsArguments = {
    return GotoTargetsArguments(source, line, None())
  }

  @pure def fromISZGotoTarget(seq: ISZ[GotoTarget]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @datatype class GotoTargetsResponseBody(
    val targets: ISZ[GotoTarget] /* The possible goto targets of the specified location. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("targets", None()), fromISZGotoTarget(targets))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZGotoTarget(ast: AST.Arr): ISZ[GotoTarget] = {
    var r = ISZ[GotoTarget]()
    for (v <- ast.values) {
      r = r :+ toGotoTarget(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toGotoTargetsResponseBody(ast: AST.Obj): GotoTargetsResponseBody = {
    val map = ast.asMap
    val targets = toISZGotoTarget(map.getArr("targets"))
    return GotoTargetsResponseBody(targets)
  }

  @pure def mkGotoTargetsResponseBody(
    targets: ISZ[GotoTarget] /* The possible goto targets of the specified location. */
  ): GotoTargetsResponseBody = {
    return GotoTargetsResponseBody(targets)
  }

  /* Response to `gotoTargets` request. */
  @datatype class GotoTargetsResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: GotoTargetsResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toGotoTargetsResponse(ast: AST.Obj): GotoTargetsResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toGotoTargetsResponseBody(map.getObj("body"))
    return GotoTargetsResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkGotoTargetsResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: GotoTargetsResponseBody
  ): GotoTargetsResponse = {
    return GotoTargetsResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    Returns a list of possible completions for a given caret position and text.
    Clients should only call this request if the corresponding capability `supportsCompletionsRequest` is true.
   */
  @datatype class CompletionsRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          completions
        }
       */,
    val arguments: CompletionsArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "completions")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toCompletionsRequest(ast: AST.Obj): CompletionsRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "completions")
    val arguments = toCompletionsArguments(map.getObj("arguments"))
    return CompletionsRequest(seq, `type`, command, arguments)
  }

  @pure def mkCompletionsRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: CompletionsArguments
  ): CompletionsRequest = {
    return CompletionsRequest(seq, "request", "completions", arguments)
  }

  /* Arguments for `completions` request. */
  @datatype class CompletionsArguments(
    val frameIdOpt: Option[Z] /* Returns completions in the scope of this stack frame. If not specified, the completions are returned for the global scope. */,
    val text: String /* One or more source lines. Typically this is the text users have typed into the debug console before they asked for completion. */,
    val column: Z /* The position within `text` for which to determine the completion proposals. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. */,
    val lineOpt: Option[Z] /* A line for which to determine the completion proposals. If missing the first line of the text is assumed. */
  ) extends `.Node` {
    @strictpure def frameId: Z = frameIdOpt.get
    @strictpure def line: Z = lineOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (frameIdOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("frameId", None()), AST.Int(frameId, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("text", None()), AST.Str(text, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("column", None()), AST.Int(column, None()))
      if (lineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("line", None()), AST.Int(line, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toCompletionsArguments(ast: AST.Obj): CompletionsArguments = {
    val map = ast.asMap
    val frameIdOpt = map.getIntValueOpt("frameId")
    val text = map.getStr("text").value
    val column = map.getInt("column").value
    val lineOpt = map.getIntValueOpt("line")
    return CompletionsArguments(frameIdOpt, text, column, lineOpt)
  }

  @pure def mkCompletionsArguments(
    text: String /* One or more source lines. Typically this is the text users have typed into the debug console before they asked for completion. */,
    column: Z /* The position within `text` for which to determine the completion proposals. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. */
  ): CompletionsArguments = {
    return CompletionsArguments(None(), text, column, None())
  }

  @pure def fromISZCompletionItem(seq: ISZ[CompletionItem]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @datatype class CompletionsResponseBody(
    val targets: ISZ[CompletionItem] /* The possible completions for . */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("targets", None()), fromISZCompletionItem(targets))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZCompletionItem(ast: AST.Arr): ISZ[CompletionItem] = {
    var r = ISZ[CompletionItem]()
    for (v <- ast.values) {
      r = r :+ toCompletionItem(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toCompletionsResponseBody(ast: AST.Obj): CompletionsResponseBody = {
    val map = ast.asMap
    val targets = toISZCompletionItem(map.getArr("targets"))
    return CompletionsResponseBody(targets)
  }

  @pure def mkCompletionsResponseBody(
    targets: ISZ[CompletionItem] /* The possible completions for . */
  ): CompletionsResponseBody = {
    return CompletionsResponseBody(targets)
  }

  /* Response to `completions` request. */
  @datatype class CompletionsResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: CompletionsResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toCompletionsResponse(ast: AST.Obj): CompletionsResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toCompletionsResponseBody(map.getObj("body"))
    return CompletionsResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkCompletionsResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: CompletionsResponseBody
  ): CompletionsResponse = {
    return CompletionsResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    Retrieves the details of the exception that caused this event to be raised.
    Clients should only call this request if the corresponding capability `supportsExceptionInfoRequest` is true.
   */
  @datatype class ExceptionInfoRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          exceptionInfo
        }
       */,
    val arguments: ExceptionInfoArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "exceptionInfo")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toExceptionInfoRequest(ast: AST.Obj): ExceptionInfoRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "exceptionInfo")
    val arguments = toExceptionInfoArguments(map.getObj("arguments"))
    return ExceptionInfoRequest(seq, `type`, command, arguments)
  }

  @pure def mkExceptionInfoRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: ExceptionInfoArguments
  ): ExceptionInfoRequest = {
    return ExceptionInfoRequest(seq, "request", "exceptionInfo", arguments)
  }

  /* Arguments for `exceptionInfo` request. */
  @datatype class ExceptionInfoArguments(
    val threadId: Z /* Thread for which exception information should be retrieved. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("threadId", None()), AST.Int(threadId, None()))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toExceptionInfoArguments(ast: AST.Obj): ExceptionInfoArguments = {
    val map = ast.asMap
    val threadId = map.getInt("threadId").value
    return ExceptionInfoArguments(threadId)
  }

  @pure def mkExceptionInfoArguments(
    threadId: Z /* Thread for which exception information should be retrieved. */
  ): ExceptionInfoArguments = {
    return ExceptionInfoArguments(threadId)
  }

  @datatype class ExceptionInfoResponseBody(
    val exceptionId: String /* ID of the exception that was thrown. */,
    val descriptionOpt: Option[String] /* Descriptive text for the exception. */,
    val breakMode: ExceptionBreakMode /* Mode that caused the exception notification to be raised. */,
    val detailsOpt: Option[ExceptionDetails] /* Detailed information about the exception. */
  ) extends `.Node` {
    @strictpure def description: String /* Descriptive text for the exception. */ = descriptionOpt.get
    @strictpure def details: ExceptionDetails /* Detailed information about the exception. */ = detailsOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("exceptionId", None()), AST.Str(exceptionId, None()))
      if (descriptionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("description", None()), AST.Str(description, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("breakMode", None()), breakMode.toAST)
      if (detailsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("details", None()), details.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toExceptionInfoResponseBody(ast: AST.Obj): ExceptionInfoResponseBody = {
    val map = ast.asMap
    val exceptionId = map.getStr("exceptionId").value
    val descriptionOpt = map.getStrValueOpt("description")
    val breakMode = toExceptionBreakMode(map.getObj("breakMode"))
    val detailsOpt = map.getObjOpt("details").map((o: AST.Obj) => toExceptionDetails(o))
    return ExceptionInfoResponseBody(exceptionId, descriptionOpt, breakMode, detailsOpt)
  }

  @pure def mkExceptionInfoResponseBody(
    exceptionId: String /* ID of the exception that was thrown. */,
    breakMode: ExceptionBreakMode /* Mode that caused the exception notification to be raised. */
  ): ExceptionInfoResponseBody = {
    return ExceptionInfoResponseBody(exceptionId, None(), breakMode, None())
  }

  /* Response to `exceptionInfo` request. */
  @datatype class ExceptionInfoResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val body: ExceptionInfoResponseBody
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toExceptionInfoResponse(ast: AST.Obj): ExceptionInfoResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val body = toExceptionInfoResponseBody(map.getObj("body"))
    return ExceptionInfoResponse(seq, `type`, request_seq, success, command, messageOpt, body)
  }

  @pure def mkExceptionInfoResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */,
    body: ExceptionInfoResponseBody
  ): ExceptionInfoResponse = {
    return ExceptionInfoResponse(seq, "response", request_seq, success, command, None(), body)
  }

  /*
    Reads bytes from memory at the provided location.
    Clients should only call this request if the corresponding capability `supportsReadMemoryRequest` is true.
   */
  @datatype class ReadMemoryRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          readMemory
        }
       */,
    val arguments: ReadMemoryArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "readMemory")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toReadMemoryRequest(ast: AST.Obj): ReadMemoryRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "readMemory")
    val arguments = toReadMemoryArguments(map.getObj("arguments"))
    return ReadMemoryRequest(seq, `type`, command, arguments)
  }

  @pure def mkReadMemoryRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: ReadMemoryArguments
  ): ReadMemoryRequest = {
    return ReadMemoryRequest(seq, "request", "readMemory", arguments)
  }

  /* Arguments for `readMemory` request. */
  @datatype class ReadMemoryArguments(
    val memoryReference: String /* Memory reference to the base location from which data should be read. */,
    val offsetOpt: Option[Z] /* Offset (in bytes) to be applied to the reference location before reading data. Can be negative. */,
    val count: Z /* Number of bytes to read at the specified location and offset. */
  ) extends `.Node` {
    @strictpure def offset: Z = offsetOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("memoryReference", None()), AST.Str(memoryReference, None()))
      if (offsetOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("offset", None()), AST.Int(offset, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("count", None()), AST.Int(count, None()))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toReadMemoryArguments(ast: AST.Obj): ReadMemoryArguments = {
    val map = ast.asMap
    val memoryReference = map.getStr("memoryReference").value
    val offsetOpt = map.getIntValueOpt("offset")
    val count = map.getInt("count").value
    return ReadMemoryArguments(memoryReference, offsetOpt, count)
  }

  @pure def mkReadMemoryArguments(
    memoryReference: String /* Memory reference to the base location from which data should be read. */,
    count: Z /* Number of bytes to read at the specified location and offset. */
  ): ReadMemoryArguments = {
    return ReadMemoryArguments(memoryReference, None(), count)
  }

  @datatype class ReadMemoryResponseBody(
    val address: String
      /*
        The address of the first byte of data returned.
        Treated as a hex value if prefixed with `0x`, or as a decimal value otherwise.
       */,
    val unreadableBytesOpt: Option[Z]
      /*
        The number of unreadable bytes encountered after the last successfully read byte.
        This can be used to determine the number of bytes that should be skipped before a subsequent `readMemory` request succeeds.
       */,
    val dataOpt: Option[String] /* The bytes read from memory, encoded using base64. If the decoded length of `data` is less than the requested `count` in the original `readMemory` request, and `unreadableBytes` is zero or omitted, then the client should assume it's reached the end of readable memory. */
  ) extends `.Node` {
    @strictpure def unreadableBytes: Z
      /*
        The number of unreadable bytes encountered after the last successfully read byte.
        This can be used to determine the number of bytes that should be skipped before a subsequent `readMemory` request succeeds.
       */ = unreadableBytesOpt.get
    @strictpure def data: String /* The bytes read from memory, encoded using base64. If the decoded length of `data` is less than the requested `count` in the original `readMemory` request, and `unreadableBytes` is zero or omitted, then the client should assume it's reached the end of readable memory. */ = dataOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("address", None()), AST.Str(address, None()))
      if (unreadableBytesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("unreadableBytes", None()), AST.Int(unreadableBytes, None()))
      }
      if (dataOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("data", None()), AST.Str(data, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toReadMemoryResponseBody(ast: AST.Obj): ReadMemoryResponseBody = {
    val map = ast.asMap
    val address = map.getStr("address").value
    val unreadableBytesOpt = map.getIntValueOpt("unreadableBytes")
    val dataOpt = map.getStrValueOpt("data")
    return ReadMemoryResponseBody(address, unreadableBytesOpt, dataOpt)
  }

  @pure def mkReadMemoryResponseBody(
    address: String
      /*
        The address of the first byte of data returned.
        Treated as a hex value if prefixed with `0x`, or as a decimal value otherwise.
       */
  ): ReadMemoryResponseBody = {
    return ReadMemoryResponseBody(address, None(), None())
  }

  /* Response to `readMemory` request. */
  @datatype class ReadMemoryResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[ReadMemoryResponseBody]
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: ReadMemoryResponseBody = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toReadMemoryResponse(ast: AST.Obj): ReadMemoryResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getObjOpt("body").map((o: AST.Obj) => toReadMemoryResponseBody(o))
    return ReadMemoryResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkReadMemoryResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): ReadMemoryResponse = {
    return ReadMemoryResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    Writes bytes to memory at the provided location.
    Clients should only call this request if the corresponding capability `supportsWriteMemoryRequest` is true.
   */
  @datatype class WriteMemoryRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          writeMemory
        }
       */,
    val arguments: WriteMemoryArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "writeMemory")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toWriteMemoryRequest(ast: AST.Obj): WriteMemoryRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "writeMemory")
    val arguments = toWriteMemoryArguments(map.getObj("arguments"))
    return WriteMemoryRequest(seq, `type`, command, arguments)
  }

  @pure def mkWriteMemoryRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: WriteMemoryArguments
  ): WriteMemoryRequest = {
    return WriteMemoryRequest(seq, "request", "writeMemory", arguments)
  }

  /* Arguments for `writeMemory` request. */
  @datatype class WriteMemoryArguments(
    val memoryReference: String /* Memory reference to the base location to which data should be written. */,
    val offsetOpt: Option[Z] /* Offset (in bytes) to be applied to the reference location before writing data. Can be negative. */,
    val allowPartialOpt: Option[B]
      /*
        Property to control partial writes. If true, the debug adapter should attempt to write memory even if the entire memory region is not writable. In such a case the debug adapter should stop after hitting the first byte of memory that cannot be written and return the number of bytes written in the response via the `offset` and `bytesWritten` properties.
        If false or missing, a debug adapter should attempt to verify the region is writable before writing, and fail the response if it is not.
       */,
    val data: String /* Bytes to write, encoded using base64. */
  ) extends `.Node` {
    @strictpure def offset: Z = offsetOpt.get
    @strictpure def allowPartial: B = allowPartialOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("memoryReference", None()), AST.Str(memoryReference, None()))
      if (offsetOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("offset", None()), AST.Int(offset, None()))
      }
      if (allowPartialOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("allowPartial", None()), AST.Bool(allowPartial, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("data", None()), AST.Str(data, None()))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toWriteMemoryArguments(ast: AST.Obj): WriteMemoryArguments = {
    val map = ast.asMap
    val memoryReference = map.getStr("memoryReference").value
    val offsetOpt = map.getIntValueOpt("offset")
    val allowPartialOpt = map.getBoolValueOpt("allowPartial")
    val data = map.getStr("data").value
    return WriteMemoryArguments(memoryReference, offsetOpt, allowPartialOpt, data)
  }

  @pure def mkWriteMemoryArguments(
    memoryReference: String /* Memory reference to the base location to which data should be written. */,
    data: String /* Bytes to write, encoded using base64. */
  ): WriteMemoryArguments = {
    return WriteMemoryArguments(memoryReference, None(), None(), data)
  }

  @datatype class WriteMemoryResponseBody(
    val offsetOpt: Option[Z] /* Property that should be returned when `allowPartial` is true to indicate the offset of the first byte of data successfully written. Can be negative. */,
    val bytesWrittenOpt: Option[Z] /* Property that should be returned when `allowPartial` is true to indicate the number of bytes starting from address that were successfully written. */
  ) extends `.Node` {
    @strictpure def offset: Z /* Property that should be returned when `allowPartial` is true to indicate the offset of the first byte of data successfully written. Can be negative. */ = offsetOpt.get
    @strictpure def bytesWritten: Z /* Property that should be returned when `allowPartial` is true to indicate the number of bytes starting from address that were successfully written. */ = bytesWrittenOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (offsetOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("offset", None()), AST.Int(offset, None()))
      }
      if (bytesWrittenOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("bytesWritten", None()), AST.Int(bytesWritten, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toWriteMemoryResponseBody(ast: AST.Obj): WriteMemoryResponseBody = {
    val map = ast.asMap
    val offsetOpt = map.getIntValueOpt("offset")
    val bytesWrittenOpt = map.getIntValueOpt("bytesWritten")
    return WriteMemoryResponseBody(offsetOpt, bytesWrittenOpt)
  }

  @pure def mkWriteMemoryResponseBody(
  ): WriteMemoryResponseBody = {
    return WriteMemoryResponseBody(None(), None())
  }

  /* Response to `writeMemory` request. */
  @datatype class WriteMemoryResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[WriteMemoryResponseBody]
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: WriteMemoryResponseBody = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toWriteMemoryResponse(ast: AST.Obj): WriteMemoryResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getObjOpt("body").map((o: AST.Obj) => toWriteMemoryResponseBody(o))
    return WriteMemoryResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkWriteMemoryResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): WriteMemoryResponse = {
    return WriteMemoryResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /*
    Disassembles code stored at the provided location.
    Clients should only call this request if the corresponding capability `supportsDisassembleRequest` is true.
   */
  @datatype class DisassembleRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          disassemble
        }
       */,
    val arguments: DisassembleArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "disassemble")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toDisassembleRequest(ast: AST.Obj): DisassembleRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "disassemble")
    val arguments = toDisassembleArguments(map.getObj("arguments"))
    return DisassembleRequest(seq, `type`, command, arguments)
  }

  @pure def mkDisassembleRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: DisassembleArguments
  ): DisassembleRequest = {
    return DisassembleRequest(seq, "request", "disassemble", arguments)
  }

  /* Arguments for `disassemble` request. */
  @datatype class DisassembleArguments(
    val memoryReference: String /* Memory reference to the base location containing the instructions to disassemble. */,
    val offsetOpt: Option[Z] /* Offset (in bytes) to be applied to the reference location before disassembling. Can be negative. */,
    val instructionOffsetOpt: Option[Z] /* Offset (in instructions) to be applied after the byte offset (if any) before disassembling. Can be negative. */,
    val instructionCount: Z
      /*
        Number of instructions to disassemble starting at the specified location and offset.
        An adapter must return exactly this number of instructions - any unavailable instructions should be replaced with an implementation-defined 'invalid instruction' value.
       */,
    val resolveSymbolsOpt: Option[B] /* If true, the adapter should attempt to resolve memory addresses and other values to symbolic names. */
  ) extends `.Node` {
    @strictpure def offset: Z = offsetOpt.get
    @strictpure def instructionOffset: Z = instructionOffsetOpt.get
    @strictpure def resolveSymbols: B = resolveSymbolsOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("memoryReference", None()), AST.Str(memoryReference, None()))
      if (offsetOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("offset", None()), AST.Int(offset, None()))
      }
      if (instructionOffsetOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("instructionOffset", None()), AST.Int(instructionOffset, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("instructionCount", None()), AST.Int(instructionCount, None()))
      if (resolveSymbolsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("resolveSymbols", None()), AST.Bool(resolveSymbols, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toDisassembleArguments(ast: AST.Obj): DisassembleArguments = {
    val map = ast.asMap
    val memoryReference = map.getStr("memoryReference").value
    val offsetOpt = map.getIntValueOpt("offset")
    val instructionOffsetOpt = map.getIntValueOpt("instructionOffset")
    val instructionCount = map.getInt("instructionCount").value
    val resolveSymbolsOpt = map.getBoolValueOpt("resolveSymbols")
    return DisassembleArguments(memoryReference, offsetOpt, instructionOffsetOpt, instructionCount, resolveSymbolsOpt)
  }

  @pure def mkDisassembleArguments(
    memoryReference: String /* Memory reference to the base location containing the instructions to disassemble. */,
    instructionCount: Z
      /*
        Number of instructions to disassemble starting at the specified location and offset.
        An adapter must return exactly this number of instructions - any unavailable instructions should be replaced with an implementation-defined 'invalid instruction' value.
       */
  ): DisassembleArguments = {
    return DisassembleArguments(memoryReference, None(), None(), instructionCount, None())
  }

  @pure def fromISZDisassembledInstruction(seq: ISZ[DisassembledInstruction]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @datatype class DisassembleResponseBody(
    val instructions: ISZ[DisassembledInstruction] /* The list of disassembled instructions. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("instructions", None()), fromISZDisassembledInstruction(instructions))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZDisassembledInstruction(ast: AST.Arr): ISZ[DisassembledInstruction] = {
    var r = ISZ[DisassembledInstruction]()
    for (v <- ast.values) {
      r = r :+ toDisassembledInstruction(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toDisassembleResponseBody(ast: AST.Obj): DisassembleResponseBody = {
    val map = ast.asMap
    val instructions = toISZDisassembledInstruction(map.getArr("instructions"))
    return DisassembleResponseBody(instructions)
  }

  @pure def mkDisassembleResponseBody(
    instructions: ISZ[DisassembledInstruction] /* The list of disassembled instructions. */
  ): DisassembleResponseBody = {
    return DisassembleResponseBody(instructions)
  }

  /* Response to `disassemble` request. */
  @datatype class DisassembleResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[DisassembleResponseBody]
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: DisassembleResponseBody = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toDisassembleResponse(ast: AST.Obj): DisassembleResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getObjOpt("body").map((o: AST.Obj) => toDisassembleResponseBody(o))
    return DisassembleResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkDisassembleResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): DisassembleResponse = {
    return DisassembleResponse(seq, "response", request_seq, success, command, None(), None())
  }

  /* Looks up information about a location reference previously returned by the debug adapter. */
  @datatype class LocationsRequest(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          request
        }
       */,
    val command: String
      /*
        Has to be one of {
          locations
        }
       */,
    val arguments: LocationsArguments
  ) extends Request {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "request")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      assert(command == "locations")
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("arguments", None()), arguments.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toLocationsRequest(ast: AST.Obj): LocationsRequest = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "request")
    val command = map.getStr("command").value
    assert(command == "locations")
    val arguments = toLocationsArguments(map.getObj("arguments"))
    return LocationsRequest(seq, `type`, command, arguments)
  }

  @pure def mkLocationsRequest(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    arguments: LocationsArguments
  ): LocationsRequest = {
    return LocationsRequest(seq, "request", "locations", arguments)
  }

  /* Arguments for `locations` request. */
  @datatype class LocationsArguments(
    val locationReference: Z /* Location reference to resolve. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("locationReference", None()), AST.Int(locationReference, None()))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toLocationsArguments(ast: AST.Obj): LocationsArguments = {
    val map = ast.asMap
    val locationReference = map.getInt("locationReference").value
    return LocationsArguments(locationReference)
  }

  @pure def mkLocationsArguments(
    locationReference: Z /* Location reference to resolve. */
  ): LocationsArguments = {
    return LocationsArguments(locationReference)
  }

  @datatype class LocationsResponseBody(
    val source: Source /* The source containing the location; either `source.path` or `source.sourceReference` must be specified. */,
    val line: Z /* The line number of the location. The client capability `linesStartAt1` determines whether it is 0- or 1-based. */,
    val columnOpt: Option[Z] /* Position of the location within the `line`. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. If no column is given, the first position in the start line is assumed. */,
    val endLineOpt: Option[Z] /* End line of the location, present if the location refers to a range.  The client capability `linesStartAt1` determines whether it is 0- or 1-based. */,
    val endColumnOpt: Option[Z] /* End position of the location within `endLine`, present if the location refers to a range. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. */
  ) extends `.Node` {
    @strictpure def column: Z /* Position of the location within the `line`. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. If no column is given, the first position in the start line is assumed. */ = columnOpt.get
    @strictpure def endLine: Z /* End line of the location, present if the location refers to a range.  The client capability `linesStartAt1` determines whether it is 0- or 1-based. */ = endLineOpt.get
    @strictpure def endColumn: Z /* End position of the location within `endLine`, present if the location refers to a range. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. */ = endColumnOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("source", None()), source.toAST)
      kvs = kvs :+ AST.KeyValue(AST.Str("line", None()), AST.Int(line, None()))
      if (columnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("column", None()), AST.Int(column, None()))
      }
      if (endLineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endLine", None()), AST.Int(endLine, None()))
      }
      if (endColumnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endColumn", None()), AST.Int(endColumn, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toLocationsResponseBody(ast: AST.Obj): LocationsResponseBody = {
    val map = ast.asMap
    val source = toSource(map.getObj("source"))
    val line = map.getInt("line").value
    val columnOpt = map.getIntValueOpt("column")
    val endLineOpt = map.getIntValueOpt("endLine")
    val endColumnOpt = map.getIntValueOpt("endColumn")
    return LocationsResponseBody(source, line, columnOpt, endLineOpt, endColumnOpt)
  }

  @pure def mkLocationsResponseBody(
    source: Source /* The source containing the location; either `source.path` or `source.sourceReference` must be specified. */,
    line: Z /* The line number of the location. The client capability `linesStartAt1` determines whether it is 0- or 1-based. */
  ): LocationsResponseBody = {
    return LocationsResponseBody(source, line, None(), None(), None())
  }

  /* Response to `locations` request. */
  @datatype class LocationsResponse(
    val seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    val `type`: String
      /*
        Has to be one of {
          response
        }
       */,
    val request_seq: Z /* Sequence number of the corresponding request. */,
    val success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    val command: String /* The command requested. */,
    val messageOpt: Option[String]
      /*
        Contains the raw error in short form if `success` is false.
        This raw error might be interpreted by the client and is not shown in the UI.
        Some predefined values exist.
        Has to be one of {
          cancelled /* the request was cancelled. */,
          notStopped /* the request may be retried once the adapter is in a 'stopped' state. */
        }
       */,
    val bodyOpt: Option[LocationsResponseBody]
  ) extends Response {
    @strictpure def message: String = messageOpt.get
    @strictpure def body: LocationsResponseBody = bodyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("seq", None()), AST.Int(seq, None()))
      assert(`type` == "response")
      kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("request_seq", None()), AST.Int(request_seq, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("success", None()), AST.Bool(success, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("command", None()), AST.Str(command, None()))
      assert(message == "cancelled" || message == "notStopped")
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (bodyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("body", None()), body.toAST)
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toLocationsResponse(ast: AST.Obj): LocationsResponse = {
    val map = ast.asMap
    val seq = map.getInt("seq").value
    val `type` = map.getStr("type").value
    assert(`type` == "response")
    val request_seq = map.getInt("request_seq").value
    val success = map.getBool("success").value
    val command = map.getStr("command").value
    val messageOpt = map.getStrValueOpt("message")
    messageOpt match {
      case Some(s) => assert(s == "cancelled" || s == "notStopped")
      case _ =>
    }
    val bodyOpt = map.getObjOpt("body").map((o: AST.Obj) => toLocationsResponseBody(o))
    return LocationsResponse(seq, `type`, request_seq, success, command, messageOpt, bodyOpt)
  }

  @pure def mkLocationsResponse(
    seq: Z /* Sequence number of the message (also known as message ID). The `seq` for the first message sent by a client or debug adapter is 1, and for each subsequent message is 1 greater than the previous message sent by that actor. `seq` can be used to order requests, responses, and events, and to associate requests with their corresponding responses. For protocol messages of type `request` the sequence number can be used to cancel the request. */,
    request_seq: Z /* Sequence number of the corresponding request. */,
    success: B
      /*
        Outcome of the request.
        If true, the request was successful and the `body` attribute may contain the result of the request.
        If the value is false, the attribute `message` contains the error in short form and the `body` may contain additional information (see `ErrorResponse.body.error`).
       */,
    command: String /* The command requested. */
  ): LocationsResponse = {
    return LocationsResponse(seq, "response", request_seq, success, command, None(), None())
  }

  @pure def fromISZExceptionBreakpointsFilter(seq: ISZ[ExceptionBreakpointsFilter]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @pure def fromISZColumnDescriptor(seq: ISZ[ColumnDescriptor]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @pure def fromISZChecksumAlgorithm(seq: ISZ[ChecksumAlgorithm]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  @pure def fromISZBreakpointMode(seq: ISZ[BreakpointMode]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  /* Types */
  /* Information about the capabilities of a debug adapter. */
  @datatype class Capabilities(
    val supportsConfigurationDoneRequestOpt: Option[B] /* The debug adapter supports the `configurationDone` request. */,
    val supportsFunctionBreakpointsOpt: Option[B] /* The debug adapter supports function breakpoints. */,
    val supportsConditionalBreakpointsOpt: Option[B] /* The debug adapter supports conditional breakpoints. */,
    val supportsHitConditionalBreakpointsOpt: Option[B] /* The debug adapter supports breakpoints that break execution after a specified number of hits. */,
    val supportsEvaluateForHoversOpt: Option[B] /* The debug adapter supports a (side effect free) `evaluate` request for data hovers. */,
    val exceptionBreakpointFiltersOpt: Option[ISZ[ExceptionBreakpointsFilter]] /* Available exception filter options for the `setExceptionBreakpoints` request. */,
    val supportsStepBackOpt: Option[B] /* The debug adapter supports stepping back via the `stepBack` and `reverseContinue` requests. */,
    val supportsSetVariableOpt: Option[B] /* The debug adapter supports setting a variable to a value. */,
    val supportsRestartFrameOpt: Option[B] /* The debug adapter supports restarting a frame. */,
    val supportsGotoTargetsRequestOpt: Option[B] /* The debug adapter supports the `gotoTargets` request. */,
    val supportsStepInTargetsRequestOpt: Option[B] /* The debug adapter supports the `stepInTargets` request. */,
    val supportsCompletionsRequestOpt: Option[B] /* The debug adapter supports the `completions` request. */,
    val completionTriggerCharactersOpt: Option[ISZ[String]] /* The set of characters that should trigger completion in a REPL. If not specified, the UI should assume the `.` character. */,
    val supportsModulesRequestOpt: Option[B] /* The debug adapter supports the `modules` request. */,
    val additionalModuleColumnsOpt: Option[ISZ[ColumnDescriptor]] /* The set of additional module information exposed by the debug adapter. */,
    val supportedChecksumAlgorithmsOpt: Option[ISZ[ChecksumAlgorithm]] /* Checksum algorithms supported by the debug adapter. */,
    val supportsRestartRequestOpt: Option[B] /* The debug adapter supports the `restart` request. In this case a client should not implement `restart` by terminating and relaunching the adapter but by calling the `restart` request. */,
    val supportsExceptionOptionsOpt: Option[B] /* The debug adapter supports `exceptionOptions` on the `setExceptionBreakpoints` request. */,
    val supportsValueFormattingOptionsOpt: Option[B] /* The debug adapter supports a `format` attribute on the `stackTrace`, `variables`, and `evaluate` requests. */,
    val supportsExceptionInfoRequestOpt: Option[B] /* The debug adapter supports the `exceptionInfo` request. */,
    val supportTerminateDebuggeeOpt: Option[B] /* The debug adapter supports the `terminateDebuggee` attribute on the `disconnect` request. */,
    val supportSuspendDebuggeeOpt: Option[B] /* The debug adapter supports the `suspendDebuggee` attribute on the `disconnect` request. */,
    val supportsDelayedStackTraceLoadingOpt: Option[B] /* The debug adapter supports the delayed loading of parts of the stack, which requires that both the `startFrame` and `levels` arguments and the `totalFrames` result of the `stackTrace` request are supported. */,
    val supportsLoadedSourcesRequestOpt: Option[B] /* The debug adapter supports the `loadedSources` request. */,
    val supportsLogPointsOpt: Option[B] /* The debug adapter supports log points by interpreting the `logMessage` attribute of the `SourceBreakpoint`. */,
    val supportsTerminateThreadsRequestOpt: Option[B] /* The debug adapter supports the `terminateThreads` request. */,
    val supportsSetExpressionOpt: Option[B] /* The debug adapter supports the `setExpression` request. */,
    val supportsTerminateRequestOpt: Option[B] /* The debug adapter supports the `terminate` request. */,
    val supportsDataBreakpointsOpt: Option[B] /* The debug adapter supports data breakpoints. */,
    val supportsReadMemoryRequestOpt: Option[B] /* The debug adapter supports the `readMemory` request. */,
    val supportsWriteMemoryRequestOpt: Option[B] /* The debug adapter supports the `writeMemory` request. */,
    val supportsDisassembleRequestOpt: Option[B] /* The debug adapter supports the `disassemble` request. */,
    val supportsCancelRequestOpt: Option[B] /* The debug adapter supports the `cancel` request. */,
    val supportsBreakpointLocationsRequestOpt: Option[B] /* The debug adapter supports the `breakpointLocations` request. */,
    val supportsClipboardContextOpt: Option[B] /* The debug adapter supports the `clipboard` context value in the `evaluate` request. */,
    val supportsSteppingGranularityOpt: Option[B] /* The debug adapter supports stepping granularities (argument `granularity`) for the stepping requests. */,
    val supportsInstructionBreakpointsOpt: Option[B] /* The debug adapter supports adding breakpoints based on instruction references. */,
    val supportsExceptionFilterOptionsOpt: Option[B] /* The debug adapter supports `filterOptions` as an argument on the `setExceptionBreakpoints` request. */,
    val supportsSingleThreadExecutionRequestsOpt: Option[B] /* The debug adapter supports the `singleThread` property on the execution requests (`continue`, `next`, `stepIn`, `stepOut`, `reverseContinue`, `stepBack`). */,
    val supportsDataBreakpointBytesOpt: Option[B] /* The debug adapter supports the `asAddress` and `bytes` fields in the `dataBreakpointInfo` request. */,
    val breakpointModesOpt: Option[ISZ[BreakpointMode]]
      /*
        Modes of breakpoints supported by the debug adapter, such as 'hardware' or 'software'. If present, the client may allow the user to select a mode and include it in its `setBreakpoints` request.
        Clients may present the first applicable mode in this array as the 'default' mode in gestures that set breakpoints.
       */
  ) extends `.Node` {
    @strictpure def supportsConfigurationDoneRequest: B = supportsConfigurationDoneRequestOpt.get
    @strictpure def supportsFunctionBreakpoints: B = supportsFunctionBreakpointsOpt.get
    @strictpure def supportsConditionalBreakpoints: B = supportsConditionalBreakpointsOpt.get
    @strictpure def supportsHitConditionalBreakpoints: B = supportsHitConditionalBreakpointsOpt.get
    @strictpure def supportsEvaluateForHovers: B = supportsEvaluateForHoversOpt.get
    @strictpure def exceptionBreakpointFilters: ISZ[ExceptionBreakpointsFilter] = exceptionBreakpointFiltersOpt.get
    @strictpure def supportsStepBack: B = supportsStepBackOpt.get
    @strictpure def supportsSetVariable: B = supportsSetVariableOpt.get
    @strictpure def supportsRestartFrame: B = supportsRestartFrameOpt.get
    @strictpure def supportsGotoTargetsRequest: B = supportsGotoTargetsRequestOpt.get
    @strictpure def supportsStepInTargetsRequest: B = supportsStepInTargetsRequestOpt.get
    @strictpure def supportsCompletionsRequest: B = supportsCompletionsRequestOpt.get
    @strictpure def completionTriggerCharacters: ISZ[String] = completionTriggerCharactersOpt.get
    @strictpure def supportsModulesRequest: B = supportsModulesRequestOpt.get
    @strictpure def additionalModuleColumns: ISZ[ColumnDescriptor] = additionalModuleColumnsOpt.get
    @strictpure def supportedChecksumAlgorithms: ISZ[ChecksumAlgorithm] = supportedChecksumAlgorithmsOpt.get
    @strictpure def supportsRestartRequest: B = supportsRestartRequestOpt.get
    @strictpure def supportsExceptionOptions: B = supportsExceptionOptionsOpt.get
    @strictpure def supportsValueFormattingOptions: B = supportsValueFormattingOptionsOpt.get
    @strictpure def supportsExceptionInfoRequest: B = supportsExceptionInfoRequestOpt.get
    @strictpure def supportTerminateDebuggee: B = supportTerminateDebuggeeOpt.get
    @strictpure def supportSuspendDebuggee: B = supportSuspendDebuggeeOpt.get
    @strictpure def supportsDelayedStackTraceLoading: B = supportsDelayedStackTraceLoadingOpt.get
    @strictpure def supportsLoadedSourcesRequest: B = supportsLoadedSourcesRequestOpt.get
    @strictpure def supportsLogPoints: B = supportsLogPointsOpt.get
    @strictpure def supportsTerminateThreadsRequest: B = supportsTerminateThreadsRequestOpt.get
    @strictpure def supportsSetExpression: B = supportsSetExpressionOpt.get
    @strictpure def supportsTerminateRequest: B = supportsTerminateRequestOpt.get
    @strictpure def supportsDataBreakpoints: B = supportsDataBreakpointsOpt.get
    @strictpure def supportsReadMemoryRequest: B = supportsReadMemoryRequestOpt.get
    @strictpure def supportsWriteMemoryRequest: B = supportsWriteMemoryRequestOpt.get
    @strictpure def supportsDisassembleRequest: B = supportsDisassembleRequestOpt.get
    @strictpure def supportsCancelRequest: B = supportsCancelRequestOpt.get
    @strictpure def supportsBreakpointLocationsRequest: B = supportsBreakpointLocationsRequestOpt.get
    @strictpure def supportsClipboardContext: B = supportsClipboardContextOpt.get
    @strictpure def supportsSteppingGranularity: B = supportsSteppingGranularityOpt.get
    @strictpure def supportsInstructionBreakpoints: B = supportsInstructionBreakpointsOpt.get
    @strictpure def supportsExceptionFilterOptions: B = supportsExceptionFilterOptionsOpt.get
    @strictpure def supportsSingleThreadExecutionRequests: B = supportsSingleThreadExecutionRequestsOpt.get
    @strictpure def supportsDataBreakpointBytes: B = supportsDataBreakpointBytesOpt.get
    @strictpure def breakpointModes: ISZ[BreakpointMode] = breakpointModesOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (supportsConfigurationDoneRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsConfigurationDoneRequest", None()), AST.Bool(supportsConfigurationDoneRequest, None()))
      }
      if (supportsFunctionBreakpointsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsFunctionBreakpoints", None()), AST.Bool(supportsFunctionBreakpoints, None()))
      }
      if (supportsConditionalBreakpointsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsConditionalBreakpoints", None()), AST.Bool(supportsConditionalBreakpoints, None()))
      }
      if (supportsHitConditionalBreakpointsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsHitConditionalBreakpoints", None()), AST.Bool(supportsHitConditionalBreakpoints, None()))
      }
      if (supportsEvaluateForHoversOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsEvaluateForHovers", None()), AST.Bool(supportsEvaluateForHovers, None()))
      }
      if (exceptionBreakpointFiltersOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("exceptionBreakpointFilters", None()), fromISZExceptionBreakpointsFilter(exceptionBreakpointFilters))
      }
      if (supportsStepBackOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsStepBack", None()), AST.Bool(supportsStepBack, None()))
      }
      if (supportsSetVariableOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsSetVariable", None()), AST.Bool(supportsSetVariable, None()))
      }
      if (supportsRestartFrameOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsRestartFrame", None()), AST.Bool(supportsRestartFrame, None()))
      }
      if (supportsGotoTargetsRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsGotoTargetsRequest", None()), AST.Bool(supportsGotoTargetsRequest, None()))
      }
      if (supportsStepInTargetsRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsStepInTargetsRequest", None()), AST.Bool(supportsStepInTargetsRequest, None()))
      }
      if (supportsCompletionsRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsCompletionsRequest", None()), AST.Bool(supportsCompletionsRequest, None()))
      }
      if (completionTriggerCharactersOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("completionTriggerCharacters", None()), fromISZString(completionTriggerCharacters))
      }
      if (supportsModulesRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsModulesRequest", None()), AST.Bool(supportsModulesRequest, None()))
      }
      if (additionalModuleColumnsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("additionalModuleColumns", None()), fromISZColumnDescriptor(additionalModuleColumns))
      }
      if (supportedChecksumAlgorithmsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportedChecksumAlgorithms", None()), fromISZChecksumAlgorithm(supportedChecksumAlgorithms))
      }
      if (supportsRestartRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsRestartRequest", None()), AST.Bool(supportsRestartRequest, None()))
      }
      if (supportsExceptionOptionsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsExceptionOptions", None()), AST.Bool(supportsExceptionOptions, None()))
      }
      if (supportsValueFormattingOptionsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsValueFormattingOptions", None()), AST.Bool(supportsValueFormattingOptions, None()))
      }
      if (supportsExceptionInfoRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsExceptionInfoRequest", None()), AST.Bool(supportsExceptionInfoRequest, None()))
      }
      if (supportTerminateDebuggeeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportTerminateDebuggee", None()), AST.Bool(supportTerminateDebuggee, None()))
      }
      if (supportSuspendDebuggeeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportSuspendDebuggee", None()), AST.Bool(supportSuspendDebuggee, None()))
      }
      if (supportsDelayedStackTraceLoadingOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsDelayedStackTraceLoading", None()), AST.Bool(supportsDelayedStackTraceLoading, None()))
      }
      if (supportsLoadedSourcesRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsLoadedSourcesRequest", None()), AST.Bool(supportsLoadedSourcesRequest, None()))
      }
      if (supportsLogPointsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsLogPoints", None()), AST.Bool(supportsLogPoints, None()))
      }
      if (supportsTerminateThreadsRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsTerminateThreadsRequest", None()), AST.Bool(supportsTerminateThreadsRequest, None()))
      }
      if (supportsSetExpressionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsSetExpression", None()), AST.Bool(supportsSetExpression, None()))
      }
      if (supportsTerminateRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsTerminateRequest", None()), AST.Bool(supportsTerminateRequest, None()))
      }
      if (supportsDataBreakpointsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsDataBreakpoints", None()), AST.Bool(supportsDataBreakpoints, None()))
      }
      if (supportsReadMemoryRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsReadMemoryRequest", None()), AST.Bool(supportsReadMemoryRequest, None()))
      }
      if (supportsWriteMemoryRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsWriteMemoryRequest", None()), AST.Bool(supportsWriteMemoryRequest, None()))
      }
      if (supportsDisassembleRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsDisassembleRequest", None()), AST.Bool(supportsDisassembleRequest, None()))
      }
      if (supportsCancelRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsCancelRequest", None()), AST.Bool(supportsCancelRequest, None()))
      }
      if (supportsBreakpointLocationsRequestOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsBreakpointLocationsRequest", None()), AST.Bool(supportsBreakpointLocationsRequest, None()))
      }
      if (supportsClipboardContextOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsClipboardContext", None()), AST.Bool(supportsClipboardContext, None()))
      }
      if (supportsSteppingGranularityOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsSteppingGranularity", None()), AST.Bool(supportsSteppingGranularity, None()))
      }
      if (supportsInstructionBreakpointsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsInstructionBreakpoints", None()), AST.Bool(supportsInstructionBreakpoints, None()))
      }
      if (supportsExceptionFilterOptionsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsExceptionFilterOptions", None()), AST.Bool(supportsExceptionFilterOptions, None()))
      }
      if (supportsSingleThreadExecutionRequestsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsSingleThreadExecutionRequests", None()), AST.Bool(supportsSingleThreadExecutionRequests, None()))
      }
      if (supportsDataBreakpointBytesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsDataBreakpointBytes", None()), AST.Bool(supportsDataBreakpointBytes, None()))
      }
      if (breakpointModesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("breakpointModes", None()), fromISZBreakpointMode(breakpointModes))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZExceptionBreakpointsFilter(ast: AST.Arr): ISZ[ExceptionBreakpointsFilter] = {
    var r = ISZ[ExceptionBreakpointsFilter]()
    for (v <- ast.values) {
      r = r :+ toExceptionBreakpointsFilter(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toISZColumnDescriptor(ast: AST.Arr): ISZ[ColumnDescriptor] = {
    var r = ISZ[ColumnDescriptor]()
    for (v <- ast.values) {
      r = r :+ toColumnDescriptor(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toISZChecksumAlgorithm(ast: AST.Arr): ISZ[ChecksumAlgorithm] = {
    var r = ISZ[ChecksumAlgorithm]()
    for (v <- ast.values) {
      r = r :+ toChecksumAlgorithm(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toISZBreakpointMode(ast: AST.Arr): ISZ[BreakpointMode] = {
    var r = ISZ[BreakpointMode]()
    for (v <- ast.values) {
      r = r :+ toBreakpointMode(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toCapabilities(ast: AST.Obj): Capabilities = {
    val map = ast.asMap
    val supportsConfigurationDoneRequestOpt = map.getBoolValueOpt("supportsConfigurationDoneRequest")
    val supportsFunctionBreakpointsOpt = map.getBoolValueOpt("supportsFunctionBreakpoints")
    val supportsConditionalBreakpointsOpt = map.getBoolValueOpt("supportsConditionalBreakpoints")
    val supportsHitConditionalBreakpointsOpt = map.getBoolValueOpt("supportsHitConditionalBreakpoints")
    val supportsEvaluateForHoversOpt = map.getBoolValueOpt("supportsEvaluateForHovers")
    val exceptionBreakpointFiltersOpt = map.getArrOpt("exceptionBreakpointFilters").map((o: AST.Arr) => toISZExceptionBreakpointsFilter(o))
    val supportsStepBackOpt = map.getBoolValueOpt("supportsStepBack")
    val supportsSetVariableOpt = map.getBoolValueOpt("supportsSetVariable")
    val supportsRestartFrameOpt = map.getBoolValueOpt("supportsRestartFrame")
    val supportsGotoTargetsRequestOpt = map.getBoolValueOpt("supportsGotoTargetsRequest")
    val supportsStepInTargetsRequestOpt = map.getBoolValueOpt("supportsStepInTargetsRequest")
    val supportsCompletionsRequestOpt = map.getBoolValueOpt("supportsCompletionsRequest")
    val completionTriggerCharactersOpt = map.getArrOpt("completionTriggerCharacters").map((o: AST.Arr) => toISZString(o))
    val supportsModulesRequestOpt = map.getBoolValueOpt("supportsModulesRequest")
    val additionalModuleColumnsOpt = map.getArrOpt("additionalModuleColumns").map((o: AST.Arr) => toISZColumnDescriptor(o))
    val supportedChecksumAlgorithmsOpt = map.getArrOpt("supportedChecksumAlgorithms").map((o: AST.Arr) => toISZChecksumAlgorithm(o))
    val supportsRestartRequestOpt = map.getBoolValueOpt("supportsRestartRequest")
    val supportsExceptionOptionsOpt = map.getBoolValueOpt("supportsExceptionOptions")
    val supportsValueFormattingOptionsOpt = map.getBoolValueOpt("supportsValueFormattingOptions")
    val supportsExceptionInfoRequestOpt = map.getBoolValueOpt("supportsExceptionInfoRequest")
    val supportTerminateDebuggeeOpt = map.getBoolValueOpt("supportTerminateDebuggee")
    val supportSuspendDebuggeeOpt = map.getBoolValueOpt("supportSuspendDebuggee")
    val supportsDelayedStackTraceLoadingOpt = map.getBoolValueOpt("supportsDelayedStackTraceLoading")
    val supportsLoadedSourcesRequestOpt = map.getBoolValueOpt("supportsLoadedSourcesRequest")
    val supportsLogPointsOpt = map.getBoolValueOpt("supportsLogPoints")
    val supportsTerminateThreadsRequestOpt = map.getBoolValueOpt("supportsTerminateThreadsRequest")
    val supportsSetExpressionOpt = map.getBoolValueOpt("supportsSetExpression")
    val supportsTerminateRequestOpt = map.getBoolValueOpt("supportsTerminateRequest")
    val supportsDataBreakpointsOpt = map.getBoolValueOpt("supportsDataBreakpoints")
    val supportsReadMemoryRequestOpt = map.getBoolValueOpt("supportsReadMemoryRequest")
    val supportsWriteMemoryRequestOpt = map.getBoolValueOpt("supportsWriteMemoryRequest")
    val supportsDisassembleRequestOpt = map.getBoolValueOpt("supportsDisassembleRequest")
    val supportsCancelRequestOpt = map.getBoolValueOpt("supportsCancelRequest")
    val supportsBreakpointLocationsRequestOpt = map.getBoolValueOpt("supportsBreakpointLocationsRequest")
    val supportsClipboardContextOpt = map.getBoolValueOpt("supportsClipboardContext")
    val supportsSteppingGranularityOpt = map.getBoolValueOpt("supportsSteppingGranularity")
    val supportsInstructionBreakpointsOpt = map.getBoolValueOpt("supportsInstructionBreakpoints")
    val supportsExceptionFilterOptionsOpt = map.getBoolValueOpt("supportsExceptionFilterOptions")
    val supportsSingleThreadExecutionRequestsOpt = map.getBoolValueOpt("supportsSingleThreadExecutionRequests")
    val supportsDataBreakpointBytesOpt = map.getBoolValueOpt("supportsDataBreakpointBytes")
    val breakpointModesOpt = map.getArrOpt("breakpointModes").map((o: AST.Arr) => toISZBreakpointMode(o))
    return Capabilities(supportsConfigurationDoneRequestOpt, supportsFunctionBreakpointsOpt, supportsConditionalBreakpointsOpt, supportsHitConditionalBreakpointsOpt, supportsEvaluateForHoversOpt, exceptionBreakpointFiltersOpt, supportsStepBackOpt, supportsSetVariableOpt, supportsRestartFrameOpt, supportsGotoTargetsRequestOpt, supportsStepInTargetsRequestOpt, supportsCompletionsRequestOpt, completionTriggerCharactersOpt, supportsModulesRequestOpt, additionalModuleColumnsOpt, supportedChecksumAlgorithmsOpt, supportsRestartRequestOpt, supportsExceptionOptionsOpt, supportsValueFormattingOptionsOpt, supportsExceptionInfoRequestOpt, supportTerminateDebuggeeOpt, supportSuspendDebuggeeOpt, supportsDelayedStackTraceLoadingOpt, supportsLoadedSourcesRequestOpt, supportsLogPointsOpt, supportsTerminateThreadsRequestOpt, supportsSetExpressionOpt, supportsTerminateRequestOpt, supportsDataBreakpointsOpt, supportsReadMemoryRequestOpt, supportsWriteMemoryRequestOpt, supportsDisassembleRequestOpt, supportsCancelRequestOpt, supportsBreakpointLocationsRequestOpt, supportsClipboardContextOpt, supportsSteppingGranularityOpt, supportsInstructionBreakpointsOpt, supportsExceptionFilterOptionsOpt, supportsSingleThreadExecutionRequestsOpt, supportsDataBreakpointBytesOpt, breakpointModesOpt)
  }

  @pure def mkCapabilities(
  ): Capabilities = {
    return Capabilities(None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None(), None())
  }

  /* An `ExceptionBreakpointsFilter` is shown in the UI as an filter option for configuring how exceptions are dealt with. */
  @datatype class ExceptionBreakpointsFilter(
    val filter: String /* The internal ID of the filter option. This value is passed to the `setExceptionBreakpoints` request. */,
    val label: String /* The name of the filter option. This is shown in the UI. */,
    val descriptionOpt: Option[String] /* A help text providing additional information about the exception filter. This string is typically shown as a hover and can be translated. */,
    val defaultOpt: Option[B] /* Initial value of the filter option. If not specified a value false is assumed. */,
    val supportsConditionOpt: Option[B] /* Controls whether a condition can be specified for this filter option. If false or missing, a condition can not be set. */,
    val conditionDescriptionOpt: Option[String] /* A help text providing information about the condition. This string is shown as the placeholder text for a text box and can be translated. */
  ) extends `.Node` {
    @strictpure def description: String = descriptionOpt.get
    @strictpure def default: B = defaultOpt.get
    @strictpure def supportsCondition: B = supportsConditionOpt.get
    @strictpure def conditionDescription: String = conditionDescriptionOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("filter", None()), AST.Str(filter, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("label", None()), AST.Str(label, None()))
      if (descriptionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("description", None()), AST.Str(description, None()))
      }
      if (defaultOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("default", None()), AST.Bool(default, None()))
      }
      if (supportsConditionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("supportsCondition", None()), AST.Bool(supportsCondition, None()))
      }
      if (conditionDescriptionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("conditionDescription", None()), AST.Str(conditionDescription, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toExceptionBreakpointsFilter(ast: AST.Obj): ExceptionBreakpointsFilter = {
    val map = ast.asMap
    val filter = map.getStr("filter").value
    val label = map.getStr("label").value
    val descriptionOpt = map.getStrValueOpt("description")
    val defaultOpt = map.getBoolValueOpt("default")
    val supportsConditionOpt = map.getBoolValueOpt("supportsCondition")
    val conditionDescriptionOpt = map.getStrValueOpt("conditionDescription")
    return ExceptionBreakpointsFilter(filter, label, descriptionOpt, defaultOpt, supportsConditionOpt, conditionDescriptionOpt)
  }

  @pure def mkExceptionBreakpointsFilter(
    filter: String /* The internal ID of the filter option. This value is passed to the `setExceptionBreakpoints` request. */,
    label: String /* The name of the filter option. This is shown in the UI. */
  ): ExceptionBreakpointsFilter = {
    return ExceptionBreakpointsFilter(filter, label, None(), None(), None(), None())
  }

  /* A structured message object. Used to return errors from requests. */
  @datatype class Message(
    val id: Z /* Unique (within a debug adapter implementation) identifier for the message. The purpose of these error IDs is to help extension authors that have the requirement that every user visible error message needs a corresponding error number, so that users or customer support can find information about the specific error more easily. */,
    val format: String
      /*
        A format string for the message. Embedded variables have the form `{name}`.
        If variable name starts with an underscore character, the variable does not contain user data (PII) and can be safely used for telemetry purposes.
       */,
    val variablesOpt: Option[`.Node`.Raw]
      /*
        An object used as a dictionary for looking up the variables in the format string.
        An object used as a dictionary for looking up the variables in the format string.
       */,
    val sendTelemetryOpt: Option[B] /* If true send to telemetry. */,
    val showUserOpt: Option[B] /* If true show user. */,
    val urlOpt: Option[String] /* A url where additional information about this message can be found. */,
    val urlLabelOpt: Option[String] /* A label that is presented to the user as the UI for opening the url. */
  ) extends `.Node` {
    @strictpure def variables: `.Node`.Raw = variablesOpt.get
    @strictpure def sendTelemetry: B = sendTelemetryOpt.get
    @strictpure def showUser: B = showUserOpt.get
    @strictpure def url: String = urlOpt.get
    @strictpure def urlLabel: String = urlLabelOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("id", None()), AST.Int(id, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("format", None()), AST.Str(format, None()))
      if (variablesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("variables", None()), variables.value)
      }
      if (sendTelemetryOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("sendTelemetry", None()), AST.Bool(sendTelemetry, None()))
      }
      if (showUserOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("showUser", None()), AST.Bool(showUser, None()))
      }
      if (urlOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("url", None()), AST.Str(url, None()))
      }
      if (urlLabelOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("urlLabel", None()), AST.Str(urlLabel, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toMessage(ast: AST.Obj): Message = {
    val map = ast.asMap
    val id = map.getInt("id").value
    val format = map.getStr("format").value
    val variablesOpt = map.getOpt("variables").map((o: AST) => `.Node`.Raw(o))
    val sendTelemetryOpt = map.getBoolValueOpt("sendTelemetry")
    val showUserOpt = map.getBoolValueOpt("showUser")
    val urlOpt = map.getStrValueOpt("url")
    val urlLabelOpt = map.getStrValueOpt("urlLabel")
    return Message(id, format, variablesOpt, sendTelemetryOpt, showUserOpt, urlOpt, urlLabelOpt)
  }

  @pure def mkMessage(
    id: Z /* Unique (within a debug adapter implementation) identifier for the message. The purpose of these error IDs is to help extension authors that have the requirement that every user visible error message needs a corresponding error number, so that users or customer support can find information about the specific error more easily. */,
    format: String
      /*
        A format string for the message. Embedded variables have the form `{name}`.
        If variable name starts with an underscore character, the variable does not contain user data (PII) and can be safely used for telemetry purposes.
       */
  ): Message = {
    return Message(id, format, None(), None(), None(), None(), None())
  }

  /*
    A Module object represents a row in the modules view.
    The `id` attribute identifies a module in the modules view and is used in a `module` event for identifying a module for adding, updating or deleting.
    The `name` attribute is used to minimally render the module in the UI.
    Additional attributes can be added to the module. They show up in the module view if they have a corresponding `ColumnDescriptor`.
    To avoid an unnecessary proliferation of additional attributes with similar semantics but different names, we recommend to re-use attributes from the 'recommended' list below first, and only introduce new attributes if nothing appropriate could be found.
   */
  @datatype class Module(
    val id: `.Node`
      /*
        Unique identifier for the module.
        Has to be any of { integer, string }
       */,
    val name: String /* A name of the module. */,
    val pathOpt: Option[String] /* Logical full path to the module. The exact definition is implementation defined, but usually this would be a full path to the on-disk file for the module. */,
    val isOptimizedOpt: Option[B] /* True if the module is optimized. */,
    val isUserCodeOpt: Option[B] /* True if the module is considered 'user code' by a debugger that supports 'Just My Code'. */,
    val versionOpt: Option[String] /* Version of Module. */,
    val symbolStatusOpt: Option[String] /* User-understandable description of if symbols were found for the module (ex: 'Symbols Loaded', 'Symbols not found', etc.) */,
    val symbolFilePathOpt: Option[String] /* Logical full path to the symbol file. The exact definition is implementation defined. */,
    val dateTimeStampOpt: Option[String] /* Module created or modified, encoded as a RFC 3339 timestamp. */,
    val addressRangeOpt: Option[String] /* Address range covered by this module. */
  ) extends `.Node` {
    @strictpure def path: String = pathOpt.get
    @strictpure def isOptimized: B = isOptimizedOpt.get
    @strictpure def isUserCode: B = isUserCodeOpt.get
    @strictpure def version: String = versionOpt.get
    @strictpure def symbolStatus: String = symbolStatusOpt.get
    @strictpure def symbolFilePath: String = symbolFilePathOpt.get
    @strictpure def dateTimeStamp: String = dateTimeStampOpt.get
    @strictpure def addressRange: String = addressRangeOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("id", None()), id.toAST)
      kvs = kvs :+ AST.KeyValue(AST.Str("name", None()), AST.Str(name, None()))
      if (pathOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("path", None()), AST.Str(path, None()))
      }
      if (isOptimizedOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("isOptimized", None()), AST.Bool(isOptimized, None()))
      }
      if (isUserCodeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("isUserCode", None()), AST.Bool(isUserCode, None()))
      }
      if (versionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("version", None()), AST.Str(version, None()))
      }
      if (symbolStatusOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("symbolStatus", None()), AST.Str(symbolStatus, None()))
      }
      if (symbolFilePathOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("symbolFilePath", None()), AST.Str(symbolFilePath, None()))
      }
      if (dateTimeStampOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("dateTimeStamp", None()), AST.Str(dateTimeStamp, None()))
      }
      if (addressRangeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("addressRange", None()), AST.Str(addressRange, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toModule(ast: AST.Obj): Module = {
    val map = ast.asMap
    val id = `.Node`.Raw(map.get("id"))
    val name = map.getStr("name").value
    val pathOpt = map.getStrValueOpt("path")
    val isOptimizedOpt = map.getBoolValueOpt("isOptimized")
    val isUserCodeOpt = map.getBoolValueOpt("isUserCode")
    val versionOpt = map.getStrValueOpt("version")
    val symbolStatusOpt = map.getStrValueOpt("symbolStatus")
    val symbolFilePathOpt = map.getStrValueOpt("symbolFilePath")
    val dateTimeStampOpt = map.getStrValueOpt("dateTimeStamp")
    val addressRangeOpt = map.getStrValueOpt("addressRange")
    return Module(id, name, pathOpt, isOptimizedOpt, isUserCodeOpt, versionOpt, symbolStatusOpt, symbolFilePathOpt, dateTimeStampOpt, addressRangeOpt)
  }

  @pure def mkModule(
    id: `.Node`
      /*
        Unique identifier for the module.
        Has to be any of { integer, string }
       */,
    name: String /* A name of the module. */
  ): Module = {
    return Module(id, name, None(), None(), None(), None(), None(), None(), None(), None())
  }

  /*
    A `ColumnDescriptor` specifies what module attribute to show in a column of the modules view, how to format it,
    and what the column's label should be.
    It is only used if the underlying UI actually supports this level of customization.
   */
  @datatype class ColumnDescriptor(
    val attributeName: String /* Name of the attribute rendered in this column. */,
    val label: String /* Header UI label of column. */,
    val formatOpt: Option[String] /* Format to use for the rendered values in this column. TBD how the format strings looks like. */,
    val typeOpt: Option[String]
      /*
        Datatype of values in this column. Defaults to `string` if not specified.
        Has to be one of {
          string,
          number,
          boolean,
          unixTimestampUTC
        }
       */,
    val widthOpt: Option[Z] /* Width of this column in characters (hint only). */
  ) extends `.Node` {
    @strictpure def format: String = formatOpt.get
    @strictpure def `type`: String = typeOpt.get
    @strictpure def width: Z = widthOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("attributeName", None()), AST.Str(attributeName, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("label", None()), AST.Str(label, None()))
      if (formatOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("format", None()), AST.Str(format, None()))
      }
      assert(`type` == "string" || `type` == "number" || `type` == "boolean" || `type` == "unixTimestampUTC")
      if (typeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      }
      if (widthOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("width", None()), AST.Int(width, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toColumnDescriptor(ast: AST.Obj): ColumnDescriptor = {
    val map = ast.asMap
    val attributeName = map.getStr("attributeName").value
    val label = map.getStr("label").value
    val formatOpt = map.getStrValueOpt("format")
    val typeOpt = map.getStrValueOpt("type")
    typeOpt match {
      case Some(s) => assert(s == "string" || s == "number" || s == "boolean" || s == "unixTimestampUTC")
      case _ =>
    }
    val widthOpt = map.getIntValueOpt("width")
    return ColumnDescriptor(attributeName, label, formatOpt, typeOpt, widthOpt)
  }

  @pure def mkColumnDescriptor(
    attributeName: String /* Name of the attribute rendered in this column. */,
    label: String /* Header UI label of column. */
  ): ColumnDescriptor = {
    return ColumnDescriptor(attributeName, label, None(), None(), None())
  }

  /* A Thread */
  @datatype class Thread(
    val id: Z /* Unique identifier for the thread. */,
    val name: String /* The name of the thread. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("id", None()), AST.Int(id, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("name", None()), AST.Str(name, None()))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toThread(ast: AST.Obj): Thread = {
    val map = ast.asMap
    val id = map.getInt("id").value
    val name = map.getStr("name").value
    return Thread(id, name)
  }

  @pure def mkThread(
    id: Z /* Unique identifier for the thread. */,
    name: String /* The name of the thread. */
  ): Thread = {
    return Thread(id, name)
  }

  @pure def fromISZChecksum(seq: ISZ[Checksum]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  /*
    A `Source` is a descriptor for source code.
    It is returned from the debug adapter as part of a `StackFrame` and it is used by clients when specifying breakpoints.
   */
  @datatype class Source(
    val nameOpt: Option[String]
      /*
        The short name of the source. Every source returned from the debug adapter has a name.
        When sending a source to the debug adapter this name is optional.
       */,
    val pathOpt: Option[String]
      /*
        The path of the source to be shown in the UI.
        It is only used to locate and load the content of the source if no `sourceReference` is specified (or its value is 0).
       */,
    val sourceReferenceOpt: Option[Z]
      /*
        If the value > 0 the contents of the source must be retrieved through the `source` request (even if a path is specified).
        Since a `sourceReference` is only valid for a session, it can not be used to persist a source.
        The value should be less than or equal to 2147483647 (2^31-1).
       */,
    val presentationHintOpt: Option[String]
      /*
        A hint for how to present the source in the UI.
        A value of `deemphasize` can be used to indicate that the source is not available or that it is skipped on stepping.
        Has to be one of {
          normal,
          emphasize,
          deemphasize
        }
       */,
    val originOpt: Option[String] /* The origin of this source. For example, 'internal module', 'inlined content from source map', etc. */,
    val sourcesOpt: Option[ISZ[Source]] /* A list of sources that are related to this source. These may be the source that generated this source. */,
    val adapterDataOpt: Option[`.Node`]
      /*
        Additional data that a debug adapter might want to loop through the client.
        The client should leave the data intact and persist it across sessions. The client should not interpret the data.
        Has to be any of { array, boolean, integer, null, number, object, string }
       */,
    val checksumsOpt: Option[ISZ[Checksum]] /* The checksums associated with this file. */
  ) extends `.Node` {
    @strictpure def name: String = nameOpt.get
    @strictpure def path: String = pathOpt.get
    @strictpure def sourceReference: Z = sourceReferenceOpt.get
    @strictpure def presentationHint: String = presentationHintOpt.get
    @strictpure def origin: String = originOpt.get
    @strictpure def sources: ISZ[Source] = sourcesOpt.get
    @strictpure def adapterData: `.Node` = adapterDataOpt.get
    @strictpure def checksums: ISZ[Checksum] = checksumsOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (nameOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("name", None()), AST.Str(name, None()))
      }
      if (pathOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("path", None()), AST.Str(path, None()))
      }
      if (sourceReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("sourceReference", None()), AST.Int(sourceReference, None()))
      }
      assert(presentationHint == "normal" || presentationHint == "emphasize" || presentationHint == "deemphasize")
      if (presentationHintOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("presentationHint", None()), AST.Str(presentationHint, None()))
      }
      if (originOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("origin", None()), AST.Str(origin, None()))
      }
      if (sourcesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("sources", None()), fromISZSource(sources))
      }
      if (adapterDataOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("adapterData", None()), adapterData.toAST)
      }
      if (checksumsOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("checksums", None()), fromISZChecksum(checksums))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZChecksum(ast: AST.Arr): ISZ[Checksum] = {
    var r = ISZ[Checksum]()
    for (v <- ast.values) {
      r = r :+ toChecksum(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toSource(ast: AST.Obj): Source = {
    val map = ast.asMap
    val nameOpt = map.getStrValueOpt("name")
    val pathOpt = map.getStrValueOpt("path")
    val sourceReferenceOpt = map.getIntValueOpt("sourceReference")
    val presentationHintOpt = map.getStrValueOpt("presentationHint")
    presentationHintOpt match {
      case Some(s) => assert(s == "normal" || s == "emphasize" || s == "deemphasize")
      case _ =>
    }
    val originOpt = map.getStrValueOpt("origin")
    val sourcesOpt = map.getArrOpt("sources").map((o: AST.Arr) => toISZSource(o))
    val adapterDataOpt = map.getOpt("adapterData").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    val checksumsOpt = map.getArrOpt("checksums").map((o: AST.Arr) => toISZChecksum(o))
    return Source(nameOpt, pathOpt, sourceReferenceOpt, presentationHintOpt, originOpt, sourcesOpt, adapterDataOpt, checksumsOpt)
  }

  @pure def mkSource(
  ): Source = {
    return Source(None(), None(), None(), None(), None(), None(), None(), None())
  }

  /* A Stackframe contains the source location. */
  @datatype class StackFrame(
    val id: Z
      /*
        An identifier for the stack frame. It must be unique across all threads.
        This id can be used to retrieve the scopes of the frame with the `scopes` request or to restart the execution of a stack frame.
       */,
    val name: String /* The name of the stack frame, typically a method name. */,
    val sourceOpt: Option[Source] /* The source of the frame. */,
    val line: Z /* The line within the source of the frame. If the source attribute is missing or doesn't exist, `line` is 0 and should be ignored by the client. */,
    val column: Z /* Start position of the range covered by the stack frame. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. If attribute `source` is missing or doesn't exist, `column` is 0 and should be ignored by the client. */,
    val endLineOpt: Option[Z] /* The end line of the range covered by the stack frame. */,
    val endColumnOpt: Option[Z] /* End position of the range covered by the stack frame. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. */,
    val canRestartOpt: Option[B] /* Indicates whether this frame can be restarted with the `restartFrame` request. Clients should only use this if the debug adapter supports the `restart` request and the corresponding capability `supportsRestartFrame` is true. If a debug adapter has this capability, then `canRestart` defaults to `true` if the property is absent. */,
    val instructionPointerReferenceOpt: Option[String] /* A memory reference for the current instruction pointer in this frame. */,
    val moduleIdOpt: Option[`.Node`]
      /*
        The module associated with this frame, if any.
        Has to be any of { integer, string }
       */,
    val presentationHintOpt: Option[String]
      /*
        A hint for how to present this frame in the UI.
        A value of `label` can be used to indicate that the frame is an artificial frame that is used as a visual label or separator. A value of `subtle` can be used to change the appearance of a frame in a 'subtle' way.
        Has to be one of {
          normal,
          label,
          subtle
        }
       */
  ) extends `.Node` {
    @strictpure def source: Source = sourceOpt.get
    @strictpure def endLine: Z = endLineOpt.get
    @strictpure def endColumn: Z = endColumnOpt.get
    @strictpure def canRestart: B = canRestartOpt.get
    @strictpure def instructionPointerReference: String = instructionPointerReferenceOpt.get
    @strictpure def moduleId: `.Node` = moduleIdOpt.get
    @strictpure def presentationHint: String = presentationHintOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("id", None()), AST.Int(id, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("name", None()), AST.Str(name, None()))
      if (sourceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("source", None()), source.toAST)
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("line", None()), AST.Int(line, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("column", None()), AST.Int(column, None()))
      if (endLineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endLine", None()), AST.Int(endLine, None()))
      }
      if (endColumnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endColumn", None()), AST.Int(endColumn, None()))
      }
      if (canRestartOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("canRestart", None()), AST.Bool(canRestart, None()))
      }
      if (instructionPointerReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("instructionPointerReference", None()), AST.Str(instructionPointerReference, None()))
      }
      if (moduleIdOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("moduleId", None()), moduleId.toAST)
      }
      assert(presentationHint == "normal" || presentationHint == "label" || presentationHint == "subtle")
      if (presentationHintOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("presentationHint", None()), AST.Str(presentationHint, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStackFrame(ast: AST.Obj): StackFrame = {
    val map = ast.asMap
    val id = map.getInt("id").value
    val name = map.getStr("name").value
    val sourceOpt = map.getObjOpt("source").map((o: AST.Obj) => toSource(o))
    val line = map.getInt("line").value
    val column = map.getInt("column").value
    val endLineOpt = map.getIntValueOpt("endLine")
    val endColumnOpt = map.getIntValueOpt("endColumn")
    val canRestartOpt = map.getBoolValueOpt("canRestart")
    val instructionPointerReferenceOpt = map.getStrValueOpt("instructionPointerReference")
    val moduleIdOpt = map.getOpt("moduleId").map((o: AST) => `.Node`.Raw(o).asInstanceOf[`.Node`])
    val presentationHintOpt = map.getStrValueOpt("presentationHint")
    presentationHintOpt match {
      case Some(s) => assert(s == "normal" || s == "label" || s == "subtle")
      case _ =>
    }
    return StackFrame(id, name, sourceOpt, line, column, endLineOpt, endColumnOpt, canRestartOpt, instructionPointerReferenceOpt, moduleIdOpt, presentationHintOpt)
  }

  @pure def mkStackFrame(
    id: Z
      /*
        An identifier for the stack frame. It must be unique across all threads.
        This id can be used to retrieve the scopes of the frame with the `scopes` request or to restart the execution of a stack frame.
       */,
    name: String /* The name of the stack frame, typically a method name. */,
    line: Z /* The line within the source of the frame. If the source attribute is missing or doesn't exist, `line` is 0 and should be ignored by the client. */,
    column: Z /* Start position of the range covered by the stack frame. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. If attribute `source` is missing or doesn't exist, `column` is 0 and should be ignored by the client. */
  ): StackFrame = {
    return StackFrame(id, name, None(), line, column, None(), None(), None(), None(), None(), None())
  }

  /* A `Scope` is a named container for variables. Optionally a scope can map to a source or a range within a source. */
  @datatype class Scope(
    val name: String /* Name of the scope such as 'Arguments', 'Locals', or 'Registers'. This string is shown in the UI as is and can be translated. */,
    val presentationHintOpt: Option[String]
      /*
        A hint for how to present this scope in the UI. If this attribute is missing, the scope is shown with a generic UI.
        Has to be one of {
          arguments /* Scope contains method arguments. */,
          locals /* Scope contains local variables. */,
          registers /* Scope contains registers. Only a single `registers` scope should be returned from a `scopes` request. */,
          returnValue /* Scope contains one or more return values. */
        }
       */,
    val variablesReference: Z /* The variables of this scope can be retrieved by passing the value of `variablesReference` to the `variables` request as long as execution remains suspended. See 'Lifetime of Object References' in the Overview section for details. */,
    val namedVariablesOpt: Option[Z]
      /*
        The number of named variables in this scope.
        The client can use this information to present the variables in a paged UI and fetch them in chunks.
       */,
    val indexedVariablesOpt: Option[Z]
      /*
        The number of indexed variables in this scope.
        The client can use this information to present the variables in a paged UI and fetch them in chunks.
       */,
    val expensive: B /* If true, the number of variables in this scope is large or expensive to retrieve. */,
    val sourceOpt: Option[Source] /* The source for this scope. */,
    val lineOpt: Option[Z] /* The start line of the range covered by this scope. */,
    val columnOpt: Option[Z] /* Start position of the range covered by the scope. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. */,
    val endLineOpt: Option[Z] /* The end line of the range covered by this scope. */,
    val endColumnOpt: Option[Z] /* End position of the range covered by the scope. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. */
  ) extends `.Node` {
    @strictpure def presentationHint: String = presentationHintOpt.get
    @strictpure def namedVariables: Z = namedVariablesOpt.get
    @strictpure def indexedVariables: Z = indexedVariablesOpt.get
    @strictpure def source: Source = sourceOpt.get
    @strictpure def line: Z = lineOpt.get
    @strictpure def column: Z = columnOpt.get
    @strictpure def endLine: Z = endLineOpt.get
    @strictpure def endColumn: Z = endColumnOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("name", None()), AST.Str(name, None()))
      assert(presentationHint == "arguments" || presentationHint == "locals" || presentationHint == "registers" || presentationHint == "returnValue")
      if (presentationHintOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("presentationHint", None()), AST.Str(presentationHint, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("variablesReference", None()), AST.Int(variablesReference, None()))
      if (namedVariablesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("namedVariables", None()), AST.Int(namedVariables, None()))
      }
      if (indexedVariablesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("indexedVariables", None()), AST.Int(indexedVariables, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("expensive", None()), AST.Bool(expensive, None()))
      if (sourceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("source", None()), source.toAST)
      }
      if (lineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("line", None()), AST.Int(line, None()))
      }
      if (columnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("column", None()), AST.Int(column, None()))
      }
      if (endLineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endLine", None()), AST.Int(endLine, None()))
      }
      if (endColumnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endColumn", None()), AST.Int(endColumn, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toScope(ast: AST.Obj): Scope = {
    val map = ast.asMap
    val name = map.getStr("name").value
    val presentationHintOpt = map.getStrValueOpt("presentationHint")
    presentationHintOpt match {
      case Some(s) => assert(s == "arguments" || s == "locals" || s == "registers" || s == "returnValue")
      case _ =>
    }
    val variablesReference = map.getInt("variablesReference").value
    val namedVariablesOpt = map.getIntValueOpt("namedVariables")
    val indexedVariablesOpt = map.getIntValueOpt("indexedVariables")
    val expensive = map.getBool("expensive").value
    val sourceOpt = map.getObjOpt("source").map((o: AST.Obj) => toSource(o))
    val lineOpt = map.getIntValueOpt("line")
    val columnOpt = map.getIntValueOpt("column")
    val endLineOpt = map.getIntValueOpt("endLine")
    val endColumnOpt = map.getIntValueOpt("endColumn")
    return Scope(name, presentationHintOpt, variablesReference, namedVariablesOpt, indexedVariablesOpt, expensive, sourceOpt, lineOpt, columnOpt, endLineOpt, endColumnOpt)
  }

  @pure def mkScope(
    name: String /* Name of the scope such as 'Arguments', 'Locals', or 'Registers'. This string is shown in the UI as is and can be translated. */,
    variablesReference: Z /* The variables of this scope can be retrieved by passing the value of `variablesReference` to the `variables` request as long as execution remains suspended. See 'Lifetime of Object References' in the Overview section for details. */,
    expensive: B /* If true, the number of variables in this scope is large or expensive to retrieve. */
  ): Scope = {
    return Scope(name, None(), variablesReference, None(), None(), expensive, None(), None(), None(), None(), None())
  }

  /*
    A Variable is a name/value pair.
    The `type` attribute is shown if space permits or when hovering over the variable's name.
    The `kind` attribute is used to render additional properties of the variable, e.g. different icons can be used to indicate that a variable is public or private.
    If the value is structured (has children), a handle is provided to retrieve the children with the `variables` request.
    If the number of named or indexed children is large, the numbers should be returned via the `namedVariables` and `indexedVariables` attributes.
    The client can use this information to present the children in a paged UI and fetch them in chunks.
   */
  @datatype class Variable(
    val name: String /* The variable's name. */,
    val value: String
      /*
        The variable's value.
        This can be a multi-line text, e.g. for a function the body of a function.
        For structured variables (which do not have a simple value), it is recommended to provide a one-line representation of the structured object. This helps to identify the structured object in the collapsed state when its children are not yet visible.
        An empty string can be used if no value should be shown in the UI.
       */,
    val typeOpt: Option[String]
      /*
        The type of the variable's value. Typically shown in the UI when hovering over the value.
        This attribute should only be returned by a debug adapter if the corresponding capability `supportsVariableType` is true.
       */,
    val presentationHintOpt: Option[VariablePresentationHint] /* Properties of a variable that can be used to determine how to render the variable in the UI. */,
    val evaluateNameOpt: Option[String] /* The evaluatable name of this variable which can be passed to the `evaluate` request to fetch the variable's value. */,
    val variablesReference: Z /* If `variablesReference` is > 0, the variable is structured and its children can be retrieved by passing `variablesReference` to the `variables` request as long as execution remains suspended. See 'Lifetime of Object References' in the Overview section for details. */,
    val namedVariablesOpt: Option[Z]
      /*
        The number of named child variables.
        The client can use this information to present the children in a paged UI and fetch them in chunks.
       */,
    val indexedVariablesOpt: Option[Z]
      /*
        The number of indexed child variables.
        The client can use this information to present the children in a paged UI and fetch them in chunks.
       */,
    val memoryReferenceOpt: Option[String]
      /*
        A memory reference associated with this variable.
        For pointer type variables, this is generally a reference to the memory address contained in the pointer.
        For executable data, this reference may later be used in a `disassemble` request.
        This attribute may be returned by a debug adapter if corresponding capability `supportsMemoryReferences` is true.
       */,
    val declarationLocationReferenceOpt: Option[Z]
      /*
        A reference that allows the client to request the location where the variable is declared. This should be present only if the adapter is likely to be able to resolve the location.
        This reference shares the same lifetime as the `variablesReference`. See 'Lifetime of Object References' in the Overview section for details.
       */,
    val valueLocationReferenceOpt: Option[Z]
      /*
        A reference that allows the client to request the location where the variable's value is declared. For example, if the variable contains a function pointer, the adapter may be able to look up the function's location. This should be present only if the adapter is likely to be able to resolve the location.
        This reference shares the same lifetime as the `variablesReference`. See 'Lifetime of Object References' in the Overview section for details.
       */
  ) extends `.Node` {
    @strictpure def `type`: String = typeOpt.get
    @strictpure def presentationHint: VariablePresentationHint = presentationHintOpt.get
    @strictpure def evaluateName: String = evaluateNameOpt.get
    @strictpure def namedVariables: Z = namedVariablesOpt.get
    @strictpure def indexedVariables: Z = indexedVariablesOpt.get
    @strictpure def memoryReference: String = memoryReferenceOpt.get
    @strictpure def declarationLocationReference: Z = declarationLocationReferenceOpt.get
    @strictpure def valueLocationReference: Z = valueLocationReferenceOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("name", None()), AST.Str(name, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("value", None()), AST.Str(value, None()))
      if (typeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), AST.Str(`type`, None()))
      }
      if (presentationHintOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("presentationHint", None()), presentationHint.toAST)
      }
      if (evaluateNameOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("evaluateName", None()), AST.Str(evaluateName, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("variablesReference", None()), AST.Int(variablesReference, None()))
      if (namedVariablesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("namedVariables", None()), AST.Int(namedVariables, None()))
      }
      if (indexedVariablesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("indexedVariables", None()), AST.Int(indexedVariables, None()))
      }
      if (memoryReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("memoryReference", None()), AST.Str(memoryReference, None()))
      }
      if (declarationLocationReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("declarationLocationReference", None()), AST.Int(declarationLocationReference, None()))
      }
      if (valueLocationReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("valueLocationReference", None()), AST.Int(valueLocationReference, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toVariable(ast: AST.Obj): Variable = {
    val map = ast.asMap
    val name = map.getStr("name").value
    val value = map.getStr("value").value
    val typeOpt = map.getStrValueOpt("type")
    val presentationHintOpt = map.getObjOpt("presentationHint").map((o: AST.Obj) => toVariablePresentationHint(o))
    val evaluateNameOpt = map.getStrValueOpt("evaluateName")
    val variablesReference = map.getInt("variablesReference").value
    val namedVariablesOpt = map.getIntValueOpt("namedVariables")
    val indexedVariablesOpt = map.getIntValueOpt("indexedVariables")
    val memoryReferenceOpt = map.getStrValueOpt("memoryReference")
    val declarationLocationReferenceOpt = map.getIntValueOpt("declarationLocationReference")
    val valueLocationReferenceOpt = map.getIntValueOpt("valueLocationReference")
    return Variable(name, value, typeOpt, presentationHintOpt, evaluateNameOpt, variablesReference, namedVariablesOpt, indexedVariablesOpt, memoryReferenceOpt, declarationLocationReferenceOpt, valueLocationReferenceOpt)
  }

  @pure def mkVariable(
    name: String /* The variable's name. */,
    value: String
      /*
        The variable's value.
        This can be a multi-line text, e.g. for a function the body of a function.
        For structured variables (which do not have a simple value), it is recommended to provide a one-line representation of the structured object. This helps to identify the structured object in the collapsed state when its children are not yet visible.
        An empty string can be used if no value should be shown in the UI.
       */,
    variablesReference: Z /* If `variablesReference` is > 0, the variable is structured and its children can be retrieved by passing `variablesReference` to the `variables` request as long as execution remains suspended. See 'Lifetime of Object References' in the Overview section for details. */
  ): Variable = {
    return Variable(name, value, None(), None(), None(), variablesReference, None(), None(), None(), None(), None())
  }

  /* Properties of a variable that can be used to determine how to render the variable in the UI. */
  @datatype class VariablePresentationHint(
    val kindOpt: Option[String]
      /*
        The kind of variable. Before introducing additional values, try to use the listed values.
        Has to be one of {
          property /* Indicates that the object is a property. */,
          method /* Indicates that the object is a method. */,
          class /* Indicates that the object is a class. */,
          data /* Indicates that the object is data. */,
          event /* Indicates that the object is an event. */,
          baseClass /* Indicates that the object is a base class. */,
          innerClass /* Indicates that the object is an inner class. */,
          interface /* Indicates that the object is an interface. */,
          mostDerivedClass /* Indicates that the object is the most derived class. */,
          virtual /* Indicates that the object is virtual, that means it is a synthetic object introduced by the adapter for rendering purposes, e.g. an index range for large arrays. */,
          dataBreakpoint /* Deprecated: Indicates that a data breakpoint is registered for the object. The `hasDataBreakpoint` attribute should generally be used instead. */
        }
       */,
    val attributesOpt: Option[ISZ[String]]
      /*
        Set of attributes represented as an array of strings. Before introducing additional values, try to use the listed values.
        Has to be one of {
          static /* Indicates that the object is static. */,
          constant /* Indicates that the object is a constant. */,
          readOnly /* Indicates that the object is read only. */,
          rawString /* Indicates that the object is a raw string. */,
          hasObjectId /* Indicates that the object can have an Object ID created for it. This is a vestigial attribute that is used by some clients; 'Object ID's are not specified in the protocol. */,
          canHaveObjectId /* Indicates that the object has an Object ID associated with it. This is a vestigial attribute that is used by some clients; 'Object ID's are not specified in the protocol. */,
          hasSideEffects /* Indicates that the evaluation had side effects. */,
          hasDataBreakpoint /* Indicates that the object has its value tracked by a data breakpoint. */
        }
       */,
    val visibilityOpt: Option[String]
      /*
        Visibility of variable. Before introducing additional values, try to use the listed values.
        Has to be one of {
          public,
          private,
          protected,
          internal,
          final
        }
       */,
    val lazyOpt: Option[B]
      /*
        If true, clients can present the variable with a UI that supports a specific gesture to trigger its evaluation.
        This mechanism can be used for properties that require executing code when retrieving their value and where the code execution can be expensive and/or produce side-effects. A typical example are properties based on a getter function.
        Please note that in addition to the `lazy` flag, the variable's `variablesReference` is expected to refer to a variable that will provide the value through another `variable` request.
       */
  ) extends `.Node` {
    @strictpure def kind: String = kindOpt.get
    @strictpure def attributes: ISZ[String] = attributesOpt.get
    @strictpure def visibility: String = visibilityOpt.get
    @strictpure def `lazy`: B = lazyOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      assert(kind == "property" || kind == "method" || kind == "class" || kind == "data" || kind == "event" || kind == "baseClass" || kind == "innerClass" || kind == "interface" || kind == "mostDerivedClass" || kind == "virtual" || kind == "dataBreakpoint")
      if (kindOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("kind", None()), AST.Str(kind, None()))
      }
      if (attributesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("attributes", None()), fromISZString(attributes))
      }
      assert(visibility == "public" || visibility == "private" || visibility == "protected" || visibility == "internal" || visibility == "final")
      if (visibilityOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("visibility", None()), AST.Str(visibility, None()))
      }
      if (lazyOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("`lazy`", None()), AST.Bool(`lazy`, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toVariablePresentationHint(ast: AST.Obj): VariablePresentationHint = {
    val map = ast.asMap
    val kindOpt = map.getStrValueOpt("kind")
    kindOpt match {
      case Some(s) => assert(s == "property" || s == "method" || s == "class" || s == "data" || s == "event" || s == "baseClass" || s == "innerClass" || s == "interface" || s == "mostDerivedClass" || s == "virtual" || s == "dataBreakpoint")
      case _ =>
    }
    val attributesOpt = map.getArrOpt("attributes").map((o: AST.Arr) => toISZString(o))
    val visibilityOpt = map.getStrValueOpt("visibility")
    visibilityOpt match {
      case Some(s) => assert(s == "public" || s == "private" || s == "protected" || s == "internal" || s == "final")
      case _ =>
    }
    val lazyOpt = map.getBoolValueOpt("lazy")
    return VariablePresentationHint(kindOpt, attributesOpt, visibilityOpt, lazyOpt)
  }

  @pure def mkVariablePresentationHint(
  ): VariablePresentationHint = {
    return VariablePresentationHint(None(), None(), None(), None())
  }

  /* Properties of a breakpoint location returned from the `breakpointLocations` request. */
  @datatype class BreakpointLocation(
    val line: Z /* Start line of breakpoint location. */,
    val columnOpt: Option[Z] /* The start position of a breakpoint location. Position is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. */,
    val endLineOpt: Option[Z] /* The end line of breakpoint location if the location covers a range. */,
    val endColumnOpt: Option[Z] /* The end position of a breakpoint location (if the location covers a range). Position is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. */
  ) extends `.Node` {
    @strictpure def column: Z = columnOpt.get
    @strictpure def endLine: Z = endLineOpt.get
    @strictpure def endColumn: Z = endColumnOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("line", None()), AST.Int(line, None()))
      if (columnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("column", None()), AST.Int(column, None()))
      }
      if (endLineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endLine", None()), AST.Int(endLine, None()))
      }
      if (endColumnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endColumn", None()), AST.Int(endColumn, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toBreakpointLocation(ast: AST.Obj): BreakpointLocation = {
    val map = ast.asMap
    val line = map.getInt("line").value
    val columnOpt = map.getIntValueOpt("column")
    val endLineOpt = map.getIntValueOpt("endLine")
    val endColumnOpt = map.getIntValueOpt("endColumn")
    return BreakpointLocation(line, columnOpt, endLineOpt, endColumnOpt)
  }

  @pure def mkBreakpointLocation(
    line: Z /* Start line of breakpoint location. */
  ): BreakpointLocation = {
    return BreakpointLocation(line, None(), None(), None())
  }

  /* Properties of a breakpoint or logpoint passed to the `setBreakpoints` request. */
  @datatype class SourceBreakpoint(
    val line: Z /* The source line of the breakpoint or logpoint. */,
    val columnOpt: Option[Z] /* Start position within source line of the breakpoint or logpoint. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. */,
    val conditionOpt: Option[String]
      /*
        The expression for conditional breakpoints.
        It is only honored by a debug adapter if the corresponding capability `supportsConditionalBreakpoints` is true.
       */,
    val hitConditionOpt: Option[String]
      /*
        The expression that controls how many hits of the breakpoint are ignored.
        The debug adapter is expected to interpret the expression as needed.
        The attribute is only honored by a debug adapter if the corresponding capability `supportsHitConditionalBreakpoints` is true.
        If both this property and `condition` are specified, `hitCondition` should be evaluated only if the `condition` is met, and the debug adapter should stop only if both conditions are met.
       */,
    val logMessageOpt: Option[String]
      /*
        If this attribute exists and is non-empty, the debug adapter must not 'break' (stop)
        but log the message instead. Expressions within `{}` are interpolated.
        The attribute is only honored by a debug adapter if the corresponding capability `supportsLogPoints` is true.
        If either `hitCondition` or `condition` is specified, then the message should only be logged if those conditions are met.
       */,
    val modeOpt: Option[String] /* The mode of this breakpoint. If defined, this must be one of the `breakpointModes` the debug adapter advertised in its `Capabilities`. */
  ) extends `.Node` {
    @strictpure def column: Z = columnOpt.get
    @strictpure def condition: String = conditionOpt.get
    @strictpure def hitCondition: String = hitConditionOpt.get
    @strictpure def logMessage: String = logMessageOpt.get
    @strictpure def mode: String = modeOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("line", None()), AST.Int(line, None()))
      if (columnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("column", None()), AST.Int(column, None()))
      }
      if (conditionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("condition", None()), AST.Str(condition, None()))
      }
      if (hitConditionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("hitCondition", None()), AST.Str(hitCondition, None()))
      }
      if (logMessageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("logMessage", None()), AST.Str(logMessage, None()))
      }
      if (modeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("mode", None()), AST.Str(mode, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSourceBreakpoint(ast: AST.Obj): SourceBreakpoint = {
    val map = ast.asMap
    val line = map.getInt("line").value
    val columnOpt = map.getIntValueOpt("column")
    val conditionOpt = map.getStrValueOpt("condition")
    val hitConditionOpt = map.getStrValueOpt("hitCondition")
    val logMessageOpt = map.getStrValueOpt("logMessage")
    val modeOpt = map.getStrValueOpt("mode")
    return SourceBreakpoint(line, columnOpt, conditionOpt, hitConditionOpt, logMessageOpt, modeOpt)
  }

  @pure def mkSourceBreakpoint(
    line: Z /* The source line of the breakpoint or logpoint. */
  ): SourceBreakpoint = {
    return SourceBreakpoint(line, None(), None(), None(), None(), None())
  }

  /* Properties of a breakpoint passed to the `setFunctionBreakpoints` request. */
  @datatype class FunctionBreakpoint(
    val name: String /* The name of the function. */,
    val conditionOpt: Option[String]
      /*
        An expression for conditional breakpoints.
        It is only honored by a debug adapter if the corresponding capability `supportsConditionalBreakpoints` is true.
       */,
    val hitConditionOpt: Option[String]
      /*
        An expression that controls how many hits of the breakpoint are ignored.
        The debug adapter is expected to interpret the expression as needed.
        The attribute is only honored by a debug adapter if the corresponding capability `supportsHitConditionalBreakpoints` is true.
       */
  ) extends `.Node` {
    @strictpure def condition: String = conditionOpt.get
    @strictpure def hitCondition: String = hitConditionOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("name", None()), AST.Str(name, None()))
      if (conditionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("condition", None()), AST.Str(condition, None()))
      }
      if (hitConditionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("hitCondition", None()), AST.Str(hitCondition, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toFunctionBreakpoint(ast: AST.Obj): FunctionBreakpoint = {
    val map = ast.asMap
    val name = map.getStr("name").value
    val conditionOpt = map.getStrValueOpt("condition")
    val hitConditionOpt = map.getStrValueOpt("hitCondition")
    return FunctionBreakpoint(name, conditionOpt, hitConditionOpt)
  }

  @pure def mkFunctionBreakpoint(
    name: String /* The name of the function. */
  ): FunctionBreakpoint = {
    return FunctionBreakpoint(name, None(), None())
  }

  /* This enumeration defines all possible access types for data breakpoints. */
  @datatype class DataBreakpointAccessType(
    val valueOpt: Option[String]
      /*
        This enumeration defines all possible access types for data breakpoints.
        Has to be one of {
          read,
          write,
          readWrite
        }
       */
  ) extends `.Node` {
    @strictpure def value: String = valueOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      assert(value == "read" || value == "write" || value == "readWrite")
      if (valueOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("value", None()), AST.Str(value, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toDataBreakpointAccessType(ast: AST.Obj): DataBreakpointAccessType = {
    val map = ast.asMap
    val valueOpt = map.getStrValueOpt("value")
    valueOpt match {
      case Some(s) => assert(s == "read" || s == "write" || s == "readWrite")
      case _ =>
    }
    return DataBreakpointAccessType(valueOpt)
  }

  @pure def mkDataBreakpointAccessType(
  ): DataBreakpointAccessType = {
    return DataBreakpointAccessType(None())
  }

  /* Properties of a data breakpoint passed to the `setDataBreakpoints` request. */
  @datatype class DataBreakpoint(
    val dataId: String /* An id representing the data. This id is returned from the `dataBreakpointInfo` request. */,
    val accessTypeOpt: Option[DataBreakpointAccessType] /* The access type of the data. */,
    val conditionOpt: Option[String] /* An expression for conditional breakpoints. */,
    val hitConditionOpt: Option[String]
      /*
        An expression that controls how many hits of the breakpoint are ignored.
        The debug adapter is expected to interpret the expression as needed.
       */
  ) extends `.Node` {
    @strictpure def accessType: DataBreakpointAccessType = accessTypeOpt.get
    @strictpure def condition: String = conditionOpt.get
    @strictpure def hitCondition: String = hitConditionOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("dataId", None()), AST.Str(dataId, None()))
      if (accessTypeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("accessType", None()), accessType.toAST)
      }
      if (conditionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("condition", None()), AST.Str(condition, None()))
      }
      if (hitConditionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("hitCondition", None()), AST.Str(hitCondition, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toDataBreakpoint(ast: AST.Obj): DataBreakpoint = {
    val map = ast.asMap
    val dataId = map.getStr("dataId").value
    val accessTypeOpt = map.getObjOpt("accessType").map((o: AST.Obj) => toDataBreakpointAccessType(o))
    val conditionOpt = map.getStrValueOpt("condition")
    val hitConditionOpt = map.getStrValueOpt("hitCondition")
    return DataBreakpoint(dataId, accessTypeOpt, conditionOpt, hitConditionOpt)
  }

  @pure def mkDataBreakpoint(
    dataId: String /* An id representing the data. This id is returned from the `dataBreakpointInfo` request. */
  ): DataBreakpoint = {
    return DataBreakpoint(dataId, None(), None(), None())
  }

  /* Properties of a breakpoint passed to the `setInstructionBreakpoints` request */
  @datatype class InstructionBreakpoint(
    val instructionReference: String
      /*
        The instruction reference of the breakpoint.
        This should be a memory or instruction pointer reference from an `EvaluateResponse`, `Variable`, `StackFrame`, `GotoTarget`, or `Breakpoint`.
       */,
    val offsetOpt: Option[Z]
      /*
        The offset from the instruction reference in bytes.
        This can be negative.
       */,
    val conditionOpt: Option[String]
      /*
        An expression for conditional breakpoints.
        It is only honored by a debug adapter if the corresponding capability `supportsConditionalBreakpoints` is true.
       */,
    val hitConditionOpt: Option[String]
      /*
        An expression that controls how many hits of the breakpoint are ignored.
        The debug adapter is expected to interpret the expression as needed.
        The attribute is only honored by a debug adapter if the corresponding capability `supportsHitConditionalBreakpoints` is true.
       */,
    val modeOpt: Option[String] /* The mode of this breakpoint. If defined, this must be one of the `breakpointModes` the debug adapter advertised in its `Capabilities`. */
  ) extends `.Node` {
    @strictpure def offset: Z = offsetOpt.get
    @strictpure def condition: String = conditionOpt.get
    @strictpure def hitCondition: String = hitConditionOpt.get
    @strictpure def mode: String = modeOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("instructionReference", None()), AST.Str(instructionReference, None()))
      if (offsetOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("offset", None()), AST.Int(offset, None()))
      }
      if (conditionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("condition", None()), AST.Str(condition, None()))
      }
      if (hitConditionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("hitCondition", None()), AST.Str(hitCondition, None()))
      }
      if (modeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("mode", None()), AST.Str(mode, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toInstructionBreakpoint(ast: AST.Obj): InstructionBreakpoint = {
    val map = ast.asMap
    val instructionReference = map.getStr("instructionReference").value
    val offsetOpt = map.getIntValueOpt("offset")
    val conditionOpt = map.getStrValueOpt("condition")
    val hitConditionOpt = map.getStrValueOpt("hitCondition")
    val modeOpt = map.getStrValueOpt("mode")
    return InstructionBreakpoint(instructionReference, offsetOpt, conditionOpt, hitConditionOpt, modeOpt)
  }

  @pure def mkInstructionBreakpoint(
    instructionReference: String
      /*
        The instruction reference of the breakpoint.
        This should be a memory or instruction pointer reference from an `EvaluateResponse`, `Variable`, `StackFrame`, `GotoTarget`, or `Breakpoint`.
       */
  ): InstructionBreakpoint = {
    return InstructionBreakpoint(instructionReference, None(), None(), None(), None())
  }

  /* Information about a breakpoint created in `setBreakpoints`, `setFunctionBreakpoints`, `setInstructionBreakpoints`, or `setDataBreakpoints` requests. */
  @datatype class Breakpoint(
    val idOpt: Option[Z] /* The identifier for the breakpoint. It is needed if breakpoint events are used to update or remove breakpoints. */,
    val verified: B /* If true, the breakpoint could be set (but not necessarily at the desired location). */,
    val messageOpt: Option[String]
      /*
        A message about the state of the breakpoint.
        This is shown to the user and can be used to explain why a breakpoint could not be verified.
       */,
    val sourceOpt: Option[Source] /* The source where the breakpoint is located. */,
    val lineOpt: Option[Z] /* The start line of the actual range covered by the breakpoint. */,
    val columnOpt: Option[Z] /* Start position of the source range covered by the breakpoint. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. */,
    val endLineOpt: Option[Z] /* The end line of the actual range covered by the breakpoint. */,
    val endColumnOpt: Option[Z]
      /*
        End position of the source range covered by the breakpoint. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based.
        If no end line is given, then the end column is assumed to be in the start line.
       */,
    val instructionReferenceOpt: Option[String] /* A memory reference to where the breakpoint is set. */,
    val offsetOpt: Option[Z]
      /*
        The offset from the instruction reference.
        This can be negative.
       */,
    val reasonOpt: Option[String]
      /*
        A machine-readable explanation of why a breakpoint may not be verified. If a breakpoint is verified or a specific reason is not known, the adapter should omit this property. Possible values include:
        - `pending`: Indicates a breakpoint might be verified in the future, but the adapter cannot verify it in the current state.
         - `failed`: Indicates a breakpoint was not able to be verified, and the adapter does not believe it can be verified without intervention.
        Has to be one of {
          pending,
          failed
        }
       */
  ) extends `.Node` {
    @strictpure def id: Z = idOpt.get
    @strictpure def message: String = messageOpt.get
    @strictpure def source: Source = sourceOpt.get
    @strictpure def line: Z = lineOpt.get
    @strictpure def column: Z = columnOpt.get
    @strictpure def endLine: Z = endLineOpt.get
    @strictpure def endColumn: Z = endColumnOpt.get
    @strictpure def instructionReference: String = instructionReferenceOpt.get
    @strictpure def offset: Z = offsetOpt.get
    @strictpure def reason: String = reasonOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (idOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("id", None()), AST.Int(id, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("verified", None()), AST.Bool(verified, None()))
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (sourceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("source", None()), source.toAST)
      }
      if (lineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("line", None()), AST.Int(line, None()))
      }
      if (columnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("column", None()), AST.Int(column, None()))
      }
      if (endLineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endLine", None()), AST.Int(endLine, None()))
      }
      if (endColumnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endColumn", None()), AST.Int(endColumn, None()))
      }
      if (instructionReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("instructionReference", None()), AST.Str(instructionReference, None()))
      }
      if (offsetOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("offset", None()), AST.Int(offset, None()))
      }
      assert(reason == "pending" || reason == "failed")
      if (reasonOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("reason", None()), AST.Str(reason, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toBreakpoint(ast: AST.Obj): Breakpoint = {
    val map = ast.asMap
    val idOpt = map.getIntValueOpt("id")
    val verified = map.getBool("verified").value
    val messageOpt = map.getStrValueOpt("message")
    val sourceOpt = map.getObjOpt("source").map((o: AST.Obj) => toSource(o))
    val lineOpt = map.getIntValueOpt("line")
    val columnOpt = map.getIntValueOpt("column")
    val endLineOpt = map.getIntValueOpt("endLine")
    val endColumnOpt = map.getIntValueOpt("endColumn")
    val instructionReferenceOpt = map.getStrValueOpt("instructionReference")
    val offsetOpt = map.getIntValueOpt("offset")
    val reasonOpt = map.getStrValueOpt("reason")
    reasonOpt match {
      case Some(s) => assert(s == "pending" || s == "failed")
      case _ =>
    }
    return Breakpoint(idOpt, verified, messageOpt, sourceOpt, lineOpt, columnOpt, endLineOpt, endColumnOpt, instructionReferenceOpt, offsetOpt, reasonOpt)
  }

  @pure def mkBreakpoint(
    verified: B /* If true, the breakpoint could be set (but not necessarily at the desired location). */
  ): Breakpoint = {
    return Breakpoint(None(), verified, None(), None(), None(), None(), None(), None(), None(), None(), None())
  }

  /* The granularity of one 'step' in the stepping requests `next`, `stepIn`, `stepOut`, and `stepBack`. */
  @datatype class SteppingGranularity(
    val valueOpt: Option[String]
      /*
        The granularity of one 'step' in the stepping requests `next`, `stepIn`, `stepOut`, and `stepBack`.
        Has to be one of {
          statement /*
            The step should allow the program to run until the current statement has finished executing.
            The meaning of a statement is determined by the adapter and it may be considered equivalent to a line.
            For example 'for(int i = 0; i < 10; i++)' could be considered to have 3 statements 'int i = 0', 'i < 10', and 'i++'.
           */,
          line /* The step should allow the program to run until the current source line has executed. */,
          instruction /* The step should allow one instruction to execute (e.g. one x86 instruction). */
        }
       */
  ) extends `.Node` {
    @strictpure def value: String = valueOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      assert(value == "statement" || value == "line" || value == "instruction")
      if (valueOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("value", None()), AST.Str(value, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toSteppingGranularity(ast: AST.Obj): SteppingGranularity = {
    val map = ast.asMap
    val valueOpt = map.getStrValueOpt("value")
    valueOpt match {
      case Some(s) => assert(s == "statement" || s == "line" || s == "instruction")
      case _ =>
    }
    return SteppingGranularity(valueOpt)
  }

  @pure def mkSteppingGranularity(
  ): SteppingGranularity = {
    return SteppingGranularity(None())
  }

  /* A `StepInTarget` can be used in the `stepIn` request and determines into which single target the `stepIn` request should step. */
  @datatype class StepInTarget(
    val id: Z /* Unique identifier for a step-in target. */,
    val label: String /* The name of the step-in target (shown in the UI). */,
    val lineOpt: Option[Z] /* The line of the step-in target. */,
    val columnOpt: Option[Z] /* Start position of the range covered by the step in target. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. */,
    val endLineOpt: Option[Z] /* The end line of the range covered by the step-in target. */,
    val endColumnOpt: Option[Z] /* End position of the range covered by the step in target. It is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. */
  ) extends `.Node` {
    @strictpure def line: Z = lineOpt.get
    @strictpure def column: Z = columnOpt.get
    @strictpure def endLine: Z = endLineOpt.get
    @strictpure def endColumn: Z = endColumnOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("id", None()), AST.Int(id, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("label", None()), AST.Str(label, None()))
      if (lineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("line", None()), AST.Int(line, None()))
      }
      if (columnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("column", None()), AST.Int(column, None()))
      }
      if (endLineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endLine", None()), AST.Int(endLine, None()))
      }
      if (endColumnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endColumn", None()), AST.Int(endColumn, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStepInTarget(ast: AST.Obj): StepInTarget = {
    val map = ast.asMap
    val id = map.getInt("id").value
    val label = map.getStr("label").value
    val lineOpt = map.getIntValueOpt("line")
    val columnOpt = map.getIntValueOpt("column")
    val endLineOpt = map.getIntValueOpt("endLine")
    val endColumnOpt = map.getIntValueOpt("endColumn")
    return StepInTarget(id, label, lineOpt, columnOpt, endLineOpt, endColumnOpt)
  }

  @pure def mkStepInTarget(
    id: Z /* Unique identifier for a step-in target. */,
    label: String /* The name of the step-in target (shown in the UI). */
  ): StepInTarget = {
    return StepInTarget(id, label, None(), None(), None(), None())
  }

  /*
    A `GotoTarget` describes a code location that can be used as a target in the `goto` request.
    The possible goto targets can be determined via the `gotoTargets` request.
   */
  @datatype class GotoTarget(
    val id: Z /* Unique identifier for a goto target. This is used in the `goto` request. */,
    val label: String /* The name of the goto target (shown in the UI). */,
    val line: Z /* The line of the goto target. */,
    val columnOpt: Option[Z] /* The column of the goto target. */,
    val endLineOpt: Option[Z] /* The end line of the range covered by the goto target. */,
    val endColumnOpt: Option[Z] /* The end column of the range covered by the goto target. */,
    val instructionPointerReferenceOpt: Option[String] /* A memory reference for the instruction pointer value represented by this target. */
  ) extends `.Node` {
    @strictpure def column: Z = columnOpt.get
    @strictpure def endLine: Z = endLineOpt.get
    @strictpure def endColumn: Z = endColumnOpt.get
    @strictpure def instructionPointerReference: String = instructionPointerReferenceOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("id", None()), AST.Int(id, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("label", None()), AST.Str(label, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("line", None()), AST.Int(line, None()))
      if (columnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("column", None()), AST.Int(column, None()))
      }
      if (endLineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endLine", None()), AST.Int(endLine, None()))
      }
      if (endColumnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endColumn", None()), AST.Int(endColumn, None()))
      }
      if (instructionPointerReferenceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("instructionPointerReference", None()), AST.Str(instructionPointerReference, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toGotoTarget(ast: AST.Obj): GotoTarget = {
    val map = ast.asMap
    val id = map.getInt("id").value
    val label = map.getStr("label").value
    val line = map.getInt("line").value
    val columnOpt = map.getIntValueOpt("column")
    val endLineOpt = map.getIntValueOpt("endLine")
    val endColumnOpt = map.getIntValueOpt("endColumn")
    val instructionPointerReferenceOpt = map.getStrValueOpt("instructionPointerReference")
    return GotoTarget(id, label, line, columnOpt, endLineOpt, endColumnOpt, instructionPointerReferenceOpt)
  }

  @pure def mkGotoTarget(
    id: Z /* Unique identifier for a goto target. This is used in the `goto` request. */,
    label: String /* The name of the goto target (shown in the UI). */,
    line: Z /* The line of the goto target. */
  ): GotoTarget = {
    return GotoTarget(id, label, line, None(), None(), None(), None())
  }

  /* `CompletionItems` are the suggestions returned from the `completions` request. */
  @datatype class CompletionItem(
    val label: String /* The label of this completion item. By default this is also the text that is inserted when selecting this completion. */,
    val textOpt: Option[String] /* If text is returned and not an empty string, then it is inserted instead of the label. */,
    val sortTextOpt: Option[String] /* A string that should be used when comparing this item with other items. If not returned or an empty string, the `label` is used instead. */,
    val detailOpt: Option[String] /* A human-readable string with additional information about this item, like type or symbol information. */,
    val typeOpt: Option[CompletionItemType] /* The item's type. Typically the client uses this information to render the item in the UI with an icon. */,
    val startOpt: Option[Z] /* Start position (within the `text` attribute of the `completions` request) where the completion text is added. The position is measured in UTF-16 code units and the client capability `columnsStartAt1` determines whether it is 0- or 1-based. If the start position is omitted the text is added at the location specified by the `column` attribute of the `completions` request. */,
    val lengthOpt: Option[Z] /* Length determines how many characters are overwritten by the completion text and it is measured in UTF-16 code units. If missing the value 0 is assumed which results in the completion text being inserted. */,
    val selectionStartOpt: Option[Z] /* Determines the start of the new selection after the text has been inserted (or replaced). `selectionStart` is measured in UTF-16 code units and must be in the range 0 and length of the completion text. If omitted the selection starts at the end of the completion text. */,
    val selectionLengthOpt: Option[Z] /* Determines the length of the new selection after the text has been inserted (or replaced) and it is measured in UTF-16 code units. The selection can not extend beyond the bounds of the completion text. If omitted the length is assumed to be 0. */
  ) extends `.Node` {
    @strictpure def text: String = textOpt.get
    @strictpure def sortText: String = sortTextOpt.get
    @strictpure def detail: String = detailOpt.get
    @strictpure def `type`: CompletionItemType = typeOpt.get
    @strictpure def start: Z = startOpt.get
    @strictpure def length: Z = lengthOpt.get
    @strictpure def selectionStart: Z = selectionStartOpt.get
    @strictpure def selectionLength: Z = selectionLengthOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("label", None()), AST.Str(label, None()))
      if (textOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("text", None()), AST.Str(text, None()))
      }
      if (sortTextOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("sortText", None()), AST.Str(sortText, None()))
      }
      if (detailOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("detail", None()), AST.Str(detail, None()))
      }
      if (typeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("`type`", None()), `type`.toAST)
      }
      if (startOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("start", None()), AST.Int(start, None()))
      }
      if (lengthOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("length", None()), AST.Int(length, None()))
      }
      if (selectionStartOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("selectionStart", None()), AST.Int(selectionStart, None()))
      }
      if (selectionLengthOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("selectionLength", None()), AST.Int(selectionLength, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toCompletionItem(ast: AST.Obj): CompletionItem = {
    val map = ast.asMap
    val label = map.getStr("label").value
    val textOpt = map.getStrValueOpt("text")
    val sortTextOpt = map.getStrValueOpt("sortText")
    val detailOpt = map.getStrValueOpt("detail")
    val typeOpt = map.getObjOpt("type").map((o: AST.Obj) => toCompletionItemType(o))
    val startOpt = map.getIntValueOpt("start")
    val lengthOpt = map.getIntValueOpt("length")
    val selectionStartOpt = map.getIntValueOpt("selectionStart")
    val selectionLengthOpt = map.getIntValueOpt("selectionLength")
    return CompletionItem(label, textOpt, sortTextOpt, detailOpt, typeOpt, startOpt, lengthOpt, selectionStartOpt, selectionLengthOpt)
  }

  @pure def mkCompletionItem(
    label: String /* The label of this completion item. By default this is also the text that is inserted when selecting this completion. */
  ): CompletionItem = {
    return CompletionItem(label, None(), None(), None(), None(), None(), None(), None(), None())
  }

  /* Some predefined types for the CompletionItem. Please note that not all clients have specific icons for all of them. */
  @datatype class CompletionItemType(
    val valueOpt: Option[String]
      /*
        Some predefined types for the CompletionItem. Please note that not all clients have specific icons for all of them.
        Has to be one of {
          method,
          function,
          constructor,
          field,
          variable,
          class,
          interface,
          module,
          property,
          unit,
          value,
          enum,
          keyword,
          snippet,
          text,
          color,
          file,
          reference,
          customcolor
        }
       */
  ) extends `.Node` {
    @strictpure def value: String = valueOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      assert(value == "method" || value == "function" || value == "constructor" || value == "field" || value == "variable" || value == "class" || value == "interface" || value == "module" || value == "property" || value == "unit" || value == "value" || value == "enum" || value == "keyword" || value == "snippet" || value == "text" || value == "color" || value == "file" || value == "reference" || value == "customcolor")
      if (valueOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("value", None()), AST.Str(value, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toCompletionItemType(ast: AST.Obj): CompletionItemType = {
    val map = ast.asMap
    val valueOpt = map.getStrValueOpt("value")
    valueOpt match {
      case Some(s) => assert(s == "method" || s == "function" || s == "constructor" || s == "field" || s == "variable" || s == "class" || s == "interface" || s == "module" || s == "property" || s == "unit" || s == "value" || s == "enum" || s == "keyword" || s == "snippet" || s == "text" || s == "color" || s == "file" || s == "reference" || s == "customcolor")
      case _ =>
    }
    return CompletionItemType(valueOpt)
  }

  @pure def mkCompletionItemType(
  ): CompletionItemType = {
    return CompletionItemType(None())
  }

  /* Names of checksum algorithms that may be supported by a debug adapter. */
  @datatype class ChecksumAlgorithm(
    val valueOpt: Option[String]
      /*
        Names of checksum algorithms that may be supported by a debug adapter.
        Has to be one of {
          MD5,
          SHA1,
          SHA256,
          timestamp
        }
       */
  ) extends `.Node` {
    @strictpure def value: String = valueOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      assert(value == "MD5" || value == "SHA1" || value == "SHA256" || value == "timestamp")
      if (valueOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("value", None()), AST.Str(value, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toChecksumAlgorithm(ast: AST.Obj): ChecksumAlgorithm = {
    val map = ast.asMap
    val valueOpt = map.getStrValueOpt("value")
    valueOpt match {
      case Some(s) => assert(s == "MD5" || s == "SHA1" || s == "SHA256" || s == "timestamp")
      case _ =>
    }
    return ChecksumAlgorithm(valueOpt)
  }

  @pure def mkChecksumAlgorithm(
  ): ChecksumAlgorithm = {
    return ChecksumAlgorithm(None())
  }

  /* The checksum of an item calculated by the specified algorithm. */
  @datatype class Checksum(
    val algorithm: ChecksumAlgorithm /* The algorithm used to calculate this checksum. */,
    val checksum: String /* Value of the checksum, encoded as a hexadecimal value. */
  ) extends `.Node` {
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("algorithm", None()), algorithm.toAST)
      kvs = kvs :+ AST.KeyValue(AST.Str("checksum", None()), AST.Str(checksum, None()))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toChecksum(ast: AST.Obj): Checksum = {
    val map = ast.asMap
    val algorithm = toChecksumAlgorithm(map.getObj("algorithm"))
    val checksum = map.getStr("checksum").value
    return Checksum(algorithm, checksum)
  }

  @pure def mkChecksum(
    algorithm: ChecksumAlgorithm /* The algorithm used to calculate this checksum. */,
    checksum: String /* Value of the checksum, encoded as a hexadecimal value. */
  ): Checksum = {
    return Checksum(algorithm, checksum)
  }

  /* Provides formatting information for a value. */
  @datatype trait ValueFormat extends `.Node` {

    def hex: B /* Display the value in hex. */

  }

  def toValueFormat(ast: AST.Obj): ValueFormat = {
    return toStackFrameFormat(ast)
  }

  /* Provides formatting information for a stack frame. */
  @datatype class StackFrameFormat(
    val hexOpt: Option[B] /* Display the value in hex. */,
    val parametersOpt: Option[B] /* Displays parameters for the stack frame. */,
    val parameterTypesOpt: Option[B] /* Displays the types of parameters for the stack frame. */,
    val parameterNamesOpt: Option[B] /* Displays the names of parameters for the stack frame. */,
    val parameterValuesOpt: Option[B] /* Displays the values of parameters for the stack frame. */,
    val lineOpt: Option[B] /* Displays the line number of the stack frame. */,
    val moduleOpt: Option[B] /* Displays the module of the stack frame. */,
    val includeAllOpt: Option[B] /* Includes all stack frames, including those the debug adapter might otherwise hide. */
  ) extends ValueFormat {
    @strictpure def hex: B = hexOpt.get
    @strictpure def parameters: B = parametersOpt.get
    @strictpure def parameterTypes: B = parameterTypesOpt.get
    @strictpure def parameterNames: B = parameterNamesOpt.get
    @strictpure def parameterValues: B = parameterValuesOpt.get
    @strictpure def line: B = lineOpt.get
    @strictpure def module: B = moduleOpt.get
    @strictpure def includeAll: B = includeAllOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (hexOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("hex", None()), AST.Bool(hex, None()))
      }
      if (parametersOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("parameters", None()), AST.Bool(parameters, None()))
      }
      if (parameterTypesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("parameterTypes", None()), AST.Bool(parameterTypes, None()))
      }
      if (parameterNamesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("parameterNames", None()), AST.Bool(parameterNames, None()))
      }
      if (parameterValuesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("parameterValues", None()), AST.Bool(parameterValues, None()))
      }
      if (lineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("line", None()), AST.Bool(line, None()))
      }
      if (moduleOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("module", None()), AST.Bool(module, None()))
      }
      if (includeAllOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("includeAll", None()), AST.Bool(includeAll, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toStackFrameFormat(ast: AST.Obj): StackFrameFormat = {
    val map = ast.asMap
    val hexOpt = map.getBoolValueOpt("hex")
    val parametersOpt = map.getBoolValueOpt("parameters")
    val parameterTypesOpt = map.getBoolValueOpt("parameterTypes")
    val parameterNamesOpt = map.getBoolValueOpt("parameterNames")
    val parameterValuesOpt = map.getBoolValueOpt("parameterValues")
    val lineOpt = map.getBoolValueOpt("line")
    val moduleOpt = map.getBoolValueOpt("module")
    val includeAllOpt = map.getBoolValueOpt("includeAll")
    return StackFrameFormat(hexOpt, parametersOpt, parameterTypesOpt, parameterNamesOpt, parameterValuesOpt, lineOpt, moduleOpt, includeAllOpt)
  }

  @pure def mkStackFrameFormat(
  ): StackFrameFormat = {
    return StackFrameFormat(None(), None(), None(), None(), None(), None(), None(), None())
  }

  /* An `ExceptionFilterOptions` is used to specify an exception filter together with a condition for the `setExceptionBreakpoints` request. */
  @datatype class ExceptionFilterOptions(
    val filterId: String /* ID of an exception filter returned by the `exceptionBreakpointFilters` capability. */,
    val conditionOpt: Option[String]
      /*
        An expression for conditional exceptions.
        The exception breaks into the debugger if the result of the condition is true.
       */,
    val modeOpt: Option[String] /* The mode of this exception breakpoint. If defined, this must be one of the `breakpointModes` the debug adapter advertised in its `Capabilities`. */
  ) extends `.Node` {
    @strictpure def condition: String = conditionOpt.get
    @strictpure def mode: String = modeOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("filterId", None()), AST.Str(filterId, None()))
      if (conditionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("condition", None()), AST.Str(condition, None()))
      }
      if (modeOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("mode", None()), AST.Str(mode, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toExceptionFilterOptions(ast: AST.Obj): ExceptionFilterOptions = {
    val map = ast.asMap
    val filterId = map.getStr("filterId").value
    val conditionOpt = map.getStrValueOpt("condition")
    val modeOpt = map.getStrValueOpt("mode")
    return ExceptionFilterOptions(filterId, conditionOpt, modeOpt)
  }

  @pure def mkExceptionFilterOptions(
    filterId: String /* ID of an exception filter returned by the `exceptionBreakpointFilters` capability. */
  ): ExceptionFilterOptions = {
    return ExceptionFilterOptions(filterId, None(), None())
  }

  @pure def fromISZExceptionPathSegment(seq: ISZ[ExceptionPathSegment]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  /* An `ExceptionOptions` assigns configuration options to a set of exceptions. */
  @datatype class ExceptionOptions(
    val pathOpt: Option[ISZ[ExceptionPathSegment]]
      /*
        A path that selects a single or multiple exceptions in a tree. If `path` is missing, the whole tree is selected.
        By convention the first segment of the path is a category that is used to group exceptions in the UI.
       */,
    val breakMode: ExceptionBreakMode /* Condition when a thrown exception should result in a break. */
  ) extends `.Node` {
    @strictpure def path: ISZ[ExceptionPathSegment] = pathOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (pathOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("path", None()), fromISZExceptionPathSegment(path))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("breakMode", None()), breakMode.toAST)
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZExceptionPathSegment(ast: AST.Arr): ISZ[ExceptionPathSegment] = {
    var r = ISZ[ExceptionPathSegment]()
    for (v <- ast.values) {
      r = r :+ toExceptionPathSegment(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toExceptionOptions(ast: AST.Obj): ExceptionOptions = {
    val map = ast.asMap
    val pathOpt = map.getArrOpt("path").map((o: AST.Arr) => toISZExceptionPathSegment(o))
    val breakMode = toExceptionBreakMode(map.getObj("breakMode"))
    return ExceptionOptions(pathOpt, breakMode)
  }

  @pure def mkExceptionOptions(
    breakMode: ExceptionBreakMode /* Condition when a thrown exception should result in a break. */
  ): ExceptionOptions = {
    return ExceptionOptions(None(), breakMode)
  }

  /*
    This enumeration defines all possible conditions when a thrown exception should result in a break.
    never: never breaks,
    always: always breaks,
    unhandled: breaks when exception unhandled,
    userUnhandled: breaks if the exception is not handled by user code.
   */
  @datatype class ExceptionBreakMode(
    val valueOpt: Option[String]
      /*
        This enumeration defines all possible conditions when a thrown exception should result in a break.
        never: never breaks,
        always: always breaks,
        unhandled: breaks when exception unhandled,
        userUnhandled: breaks if the exception is not handled by user code.
        Has to be one of {
          never,
          always,
          unhandled,
          userUnhandled
        }
       */
  ) extends `.Node` {
    @strictpure def value: String = valueOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      assert(value == "never" || value == "always" || value == "unhandled" || value == "userUnhandled")
      if (valueOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("value", None()), AST.Str(value, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toExceptionBreakMode(ast: AST.Obj): ExceptionBreakMode = {
    val map = ast.asMap
    val valueOpt = map.getStrValueOpt("value")
    valueOpt match {
      case Some(s) => assert(s == "never" || s == "always" || s == "unhandled" || s == "userUnhandled")
      case _ =>
    }
    return ExceptionBreakMode(valueOpt)
  }

  @pure def mkExceptionBreakMode(
  ): ExceptionBreakMode = {
    return ExceptionBreakMode(None())
  }

  /*
    An `ExceptionPathSegment` represents a segment in a path that is used to match leafs or nodes in a tree of exceptions.
    If a segment consists of more than one name, it matches the names provided if `negate` is false or missing, or it matches anything except the names provided if `negate` is true.
   */
  @datatype class ExceptionPathSegment(
    val negateOpt: Option[B] /* If false or missing this segment matches the names provided, otherwise it matches anything except the names provided. */,
    val names: ISZ[String] /* Depending on the value of `negate` the names that should match or not match. */
  ) extends `.Node` {
    @strictpure def negate: B = negateOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (negateOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("negate", None()), AST.Bool(negate, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("names", None()), fromISZString(names))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toExceptionPathSegment(ast: AST.Obj): ExceptionPathSegment = {
    val map = ast.asMap
    val negateOpt = map.getBoolValueOpt("negate")
    val names = toISZString(map.getArr("names"))
    return ExceptionPathSegment(negateOpt, names)
  }

  @pure def mkExceptionPathSegment(
    names: ISZ[String] /* Depending on the value of `negate` the names that should match or not match. */
  ): ExceptionPathSegment = {
    return ExceptionPathSegment(None(), names)
  }

  @pure def fromISZExceptionDetails(seq: ISZ[ExceptionDetails]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  /* Detailed information about an exception that has occurred. */
  @datatype class ExceptionDetails(
    val messageOpt: Option[String] /* Message contained in the exception. */,
    val typeNameOpt: Option[String] /* Short type name of the exception object. */,
    val fullTypeNameOpt: Option[String] /* Fully-qualified type name of the exception object. */,
    val evaluateNameOpt: Option[String] /* An expression that can be evaluated in the current scope to obtain the exception object. */,
    val stackTraceOpt: Option[String] /* Stack trace at the time the exception was thrown. */,
    val innerExceptionOpt: Option[ISZ[ExceptionDetails]] /* Details of the exception contained by this exception, if any. */
  ) extends `.Node` {
    @strictpure def message: String = messageOpt.get
    @strictpure def typeName: String = typeNameOpt.get
    @strictpure def fullTypeName: String = fullTypeNameOpt.get
    @strictpure def evaluateName: String = evaluateNameOpt.get
    @strictpure def stackTrace: String = stackTraceOpt.get
    @strictpure def innerException: ISZ[ExceptionDetails] = innerExceptionOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      if (messageOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("message", None()), AST.Str(message, None()))
      }
      if (typeNameOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("typeName", None()), AST.Str(typeName, None()))
      }
      if (fullTypeNameOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("fullTypeName", None()), AST.Str(fullTypeName, None()))
      }
      if (evaluateNameOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("evaluateName", None()), AST.Str(evaluateName, None()))
      }
      if (stackTraceOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("stackTrace", None()), AST.Str(stackTrace, None()))
      }
      if (innerExceptionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("innerException", None()), fromISZExceptionDetails(innerException))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZExceptionDetails(ast: AST.Arr): ISZ[ExceptionDetails] = {
    var r = ISZ[ExceptionDetails]()
    for (v <- ast.values) {
      r = r :+ toExceptionDetails(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toExceptionDetails(ast: AST.Obj): ExceptionDetails = {
    val map = ast.asMap
    val messageOpt = map.getStrValueOpt("message")
    val typeNameOpt = map.getStrValueOpt("typeName")
    val fullTypeNameOpt = map.getStrValueOpt("fullTypeName")
    val evaluateNameOpt = map.getStrValueOpt("evaluateName")
    val stackTraceOpt = map.getStrValueOpt("stackTrace")
    val innerExceptionOpt = map.getArrOpt("innerException").map((o: AST.Arr) => toISZExceptionDetails(o))
    return ExceptionDetails(messageOpt, typeNameOpt, fullTypeNameOpt, evaluateNameOpt, stackTraceOpt, innerExceptionOpt)
  }

  @pure def mkExceptionDetails(
  ): ExceptionDetails = {
    return ExceptionDetails(None(), None(), None(), None(), None(), None())
  }

  /* Represents a single disassembled instruction. */
  @datatype class DisassembledInstruction(
    val address: String /* The address of the instruction. Treated as a hex value if prefixed with `0x`, or as a decimal value otherwise. */,
    val instructionBytesOpt: Option[String] /* Raw bytes representing the instruction and its operands, in an implementation-defined format. */,
    val instruction: String /* Text representing the instruction and its operands, in an implementation-defined format. */,
    val symbolOpt: Option[String] /* Name of the symbol that corresponds with the location of this instruction, if any. */,
    val locationOpt: Option[Source]
      /*
        Source location that corresponds to this instruction, if any.
        Should always be set (if available) on the first instruction returned,
        but can be omitted afterwards if this instruction maps to the same source file as the previous instruction.
       */,
    val lineOpt: Option[Z] /* The line within the source location that corresponds to this instruction, if any. */,
    val columnOpt: Option[Z] /* The column within the line that corresponds to this instruction, if any. */,
    val endLineOpt: Option[Z] /* The end line of the range that corresponds to this instruction, if any. */,
    val endColumnOpt: Option[Z] /* The end column of the range that corresponds to this instruction, if any. */,
    val presentationHintOpt: Option[String]
      /*
        A hint for how to present the instruction in the UI.
        A value of `invalid` may be used to indicate this instruction is 'filler' and cannot be reached by the program. For example, unreadable memory addresses may be presented is 'invalid.'
        Has to be one of {
          normal,
          invalid
        }
       */
  ) extends `.Node` {
    @strictpure def instructionBytes: String = instructionBytesOpt.get
    @strictpure def symbol: String = symbolOpt.get
    @strictpure def location: Source = locationOpt.get
    @strictpure def line: Z = lineOpt.get
    @strictpure def column: Z = columnOpt.get
    @strictpure def endLine: Z = endLineOpt.get
    @strictpure def endColumn: Z = endColumnOpt.get
    @strictpure def presentationHint: String = presentationHintOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("address", None()), AST.Str(address, None()))
      if (instructionBytesOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("instructionBytes", None()), AST.Str(instructionBytes, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("instruction", None()), AST.Str(instruction, None()))
      if (symbolOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("symbol", None()), AST.Str(symbol, None()))
      }
      if (locationOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("location", None()), location.toAST)
      }
      if (lineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("line", None()), AST.Int(line, None()))
      }
      if (columnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("column", None()), AST.Int(column, None()))
      }
      if (endLineOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endLine", None()), AST.Int(endLine, None()))
      }
      if (endColumnOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("endColumn", None()), AST.Int(endColumn, None()))
      }
      assert(presentationHint == "normal" || presentationHint == "invalid")
      if (presentationHintOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("presentationHint", None()), AST.Str(presentationHint, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toDisassembledInstruction(ast: AST.Obj): DisassembledInstruction = {
    val map = ast.asMap
    val address = map.getStr("address").value
    val instructionBytesOpt = map.getStrValueOpt("instructionBytes")
    val instruction = map.getStr("instruction").value
    val symbolOpt = map.getStrValueOpt("symbol")
    val locationOpt = map.getObjOpt("location").map((o: AST.Obj) => toSource(o))
    val lineOpt = map.getIntValueOpt("line")
    val columnOpt = map.getIntValueOpt("column")
    val endLineOpt = map.getIntValueOpt("endLine")
    val endColumnOpt = map.getIntValueOpt("endColumn")
    val presentationHintOpt = map.getStrValueOpt("presentationHint")
    presentationHintOpt match {
      case Some(s) => assert(s == "normal" || s == "invalid")
      case _ =>
    }
    return DisassembledInstruction(address, instructionBytesOpt, instruction, symbolOpt, locationOpt, lineOpt, columnOpt, endLineOpt, endColumnOpt, presentationHintOpt)
  }

  @pure def mkDisassembledInstruction(
    address: String /* The address of the instruction. Treated as a hex value if prefixed with `0x`, or as a decimal value otherwise. */,
    instruction: String /* Text representing the instruction and its operands, in an implementation-defined format. */
  ): DisassembledInstruction = {
    return DisassembledInstruction(address, None(), instruction, None(), None(), None(), None(), None(), None(), None())
  }

  /* Logical areas that can be invalidated by the `invalidated` event. */
  @datatype class InvalidatedAreas(
    val valueOpt: Option[String]
      /*
        Logical areas that can be invalidated by the `invalidated` event.
        Has to be one of {
          all /* All previously fetched data has become invalid and needs to be refetched. */,
          stacks /* Previously fetched stack related data has become invalid and needs to be refetched. */,
          threads /* Previously fetched thread related data has become invalid and needs to be refetched. */,
          variables /* Previously fetched variable data has become invalid and needs to be refetched. */
        }
       */
  ) extends `.Node` {
    @strictpure def value: String = valueOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      assert(value == "all" || value == "stacks" || value == "threads" || value == "variables")
      if (valueOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("value", None()), AST.Str(value, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toInvalidatedAreas(ast: AST.Obj): InvalidatedAreas = {
    val map = ast.asMap
    val valueOpt = map.getStrValueOpt("value")
    valueOpt match {
      case Some(s) => assert(s == "all" || s == "stacks" || s == "threads" || s == "variables")
      case _ =>
    }
    return InvalidatedAreas(valueOpt)
  }

  @pure def mkInvalidatedAreas(
  ): InvalidatedAreas = {
    return InvalidatedAreas(None())
  }

  @pure def fromISZBreakpointModeApplicability(seq: ISZ[BreakpointModeApplicability]): AST.Arr = {
    var elements = ISZ[AST]()
    for (v <- seq) {
      elements = elements :+ v.toAST
    }
    return AST.Arr(elements, None())
  }

  /* A `BreakpointMode` is provided as a option when setting breakpoints on sources or instructions. */
  @datatype class BreakpointMode(
    val mode: String /* The internal ID of the mode. This value is passed to the `setBreakpoints` request. */,
    val label: String /* The name of the breakpoint mode. This is shown in the UI. */,
    val descriptionOpt: Option[String] /* A help text providing additional information about the breakpoint mode. This string is typically shown as a hover and can be translated. */,
    val appliesTo: ISZ[BreakpointModeApplicability] /* Describes one or more type of breakpoint this mode applies to. */
  ) extends `.Node` {
    @strictpure def description: String = descriptionOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      kvs = kvs :+ AST.KeyValue(AST.Str("mode", None()), AST.Str(mode, None()))
      kvs = kvs :+ AST.KeyValue(AST.Str("label", None()), AST.Str(label, None()))
      if (descriptionOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("description", None()), AST.Str(description, None()))
      }
      kvs = kvs :+ AST.KeyValue(AST.Str("appliesTo", None()), fromISZBreakpointModeApplicability(appliesTo))
      return AST.Obj(kvs, None())
    }
  }

  @pure def toISZBreakpointModeApplicability(ast: AST.Arr): ISZ[BreakpointModeApplicability] = {
    var r = ISZ[BreakpointModeApplicability]()
    for (v <- ast.values) {
      r = r :+ toBreakpointModeApplicability(v.asInstanceOf[AST.Obj])
    }
    return r
  }

  @pure def toBreakpointMode(ast: AST.Obj): BreakpointMode = {
    val map = ast.asMap
    val mode = map.getStr("mode").value
    val label = map.getStr("label").value
    val descriptionOpt = map.getStrValueOpt("description")
    val appliesTo = toISZBreakpointModeApplicability(map.getArr("appliesTo"))
    return BreakpointMode(mode, label, descriptionOpt, appliesTo)
  }

  @pure def mkBreakpointMode(
    mode: String /* The internal ID of the mode. This value is passed to the `setBreakpoints` request. */,
    label: String /* The name of the breakpoint mode. This is shown in the UI. */,
    appliesTo: ISZ[BreakpointModeApplicability] /* Describes one or more type of breakpoint this mode applies to. */
  ): BreakpointMode = {
    return BreakpointMode(mode, label, None(), appliesTo)
  }

  /* Describes one or more type of breakpoint a `BreakpointMode` applies to. This is a non-exhaustive enumeration and may expand as future breakpoint types are added. */
  @datatype class BreakpointModeApplicability(
    val valueOpt: Option[String]
      /*
        Describes one or more type of breakpoint a `BreakpointMode` applies to. This is a non-exhaustive enumeration and may expand as future breakpoint types are added.
        Has to be one of {
          source /* In `SourceBreakpoint`s */,
          exception /* In exception breakpoints applied in the `ExceptionFilterOptions` */,
          data /* In data breakpoints requested in the `DataBreakpointInfo` request */,
          instruction /* In `InstructionBreakpoint`s */
        }
       */
  ) extends `.Node` {
    @strictpure def value: String = valueOpt.get
    @pure def toAST: AST.Obj = {
      var kvs = ISZ[AST.KeyValue]()
      assert(value == "source" || value == "exception" || value == "data" || value == "instruction")
      if (valueOpt.nonEmpty) {
        kvs = kvs :+ AST.KeyValue(AST.Str("value", None()), AST.Str(value, None()))
      }
      return AST.Obj(kvs, None())
    }
  }

  @pure def toBreakpointModeApplicability(ast: AST.Obj): BreakpointModeApplicability = {
    val map = ast.asMap
    val valueOpt = map.getStrValueOpt("value")
    valueOpt match {
      case Some(s) => assert(s == "source" || s == "exception" || s == "data" || s == "instruction")
      case _ =>
    }
    return BreakpointModeApplicability(valueOpt)
  }

  @pure def mkBreakpointModeApplicability(
  ): BreakpointModeApplicability = {
    return BreakpointModeApplicability(None())
  }

}