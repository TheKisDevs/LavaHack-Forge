/*
 * Copyright (c) 2010-2020 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

package com.kisman.cc.loader.websockets.api;

import com.kisman.cc.loader.websockets.api.WebSocket;
import com.kisman.cc.loader.websockets.api.WebSocketListener;
import com.kisman.cc.loader.websockets.api.drafts.Draft;
import com.kisman.cc.loader.websockets.api.exceptions.InvalidDataException;
import com.kisman.cc.loader.websockets.api.framing.Framedata;
import com.kisman.cc.loader.websockets.api.framing.PingFrame;
import com.kisman.cc.loader.websockets.api.framing.PongFrame;
import com.kisman.cc.loader.websockets.api.handshake.ClientHandshake;
import com.kisman.cc.loader.websockets.api.handshake.HandshakeImpl1Server;
import com.kisman.cc.loader.websockets.api.handshake.ServerHandshake;
import com.kisman.cc.loader.websockets.api.handshake.ServerHandshakeBuilder;

/**
 * This class default implements all methods of the WebSocketListener that can be overridden
 * optionally when advances functionalities is needed.<br>
 **/
public abstract class WebSocketAdapter implements WebSocketListener {

  private PingFrame pingFrame;

  /**
   * This default implementation does not do anything. Go ahead and overwrite it.
   *
   * @see WebSocketListener#onWebsocketHandshakeReceivedAsServer(com.kisman.cc.loader.websockets.api.WebSocket,
   * Draft, ClientHandshake)
   */
  @Override
  public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(com.kisman.cc.loader.websockets.api.WebSocket conn, Draft draft,
                                                                     ClientHandshake request) throws InvalidDataException {
    return new HandshakeImpl1Server();
  }

  @Override
  public void onWebsocketHandshakeReceivedAsClient(com.kisman.cc.loader.websockets.api.WebSocket conn, ClientHandshake request,
                                                   ServerHandshake response) throws InvalidDataException {
    //To overwrite
  }

  /**
   * This default implementation does not do anything which will cause the connections to always
   * progress.
   *
   * @see WebSocketListener#onWebsocketHandshakeSentAsClient(com.kisman.cc.loader.websockets.api.WebSocket,
   * ClientHandshake)
   */
  @Override
  public void onWebsocketHandshakeSentAsClient(com.kisman.cc.loader.websockets.api.WebSocket conn, ClientHandshake request)
      throws InvalidDataException {
    //To overwrite
  }

  /**
   * This default implementation will send a pong in response to the received ping. The pong frame
   * will have the same payload as the ping frame.
   *
   * @see WebSocketListener#onWebsocketPing(com.kisman.cc.loader.websockets.api.WebSocket, Framedata)
   */
  @Override
  public void onWebsocketPing(com.kisman.cc.loader.websockets.api.WebSocket conn, Framedata f) {
    conn.sendFrame(new PongFrame((PingFrame) f));
  }

  /**
   * This default implementation does not do anything. Go ahead and overwrite it.
   *
   * @see WebSocketListener#onWebsocketPong(com.kisman.cc.loader.websockets.api.WebSocket, Framedata)
   */
  @Override
  public void onWebsocketPong(com.kisman.cc.loader.websockets.api.WebSocket conn, Framedata f) {
    //To overwrite
  }

  /**
   * Default implementation for onPreparePing, returns a (cached) PingFrame that has no application
   * data.
   *
   * @param conn The <tt>WebSocket</tt> connection from which the ping frame will be sent.
   * @return PingFrame to be sent.
   * @see WebSocketListener#onPreparePing(com.kisman.cc.loader.websockets.api.WebSocket)
   */
  @Override
  public PingFrame onPreparePing(WebSocket conn) {
    if (pingFrame == null) {
      pingFrame = new PingFrame();
    }
    return pingFrame;
  }
}
