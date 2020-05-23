/*
 * Copyright 2020 Jay Ehsaniara
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ehsaniara.s3;

import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.events.SessionEvent;
import org.apache.maven.wagon.events.SessionListener;

import java.util.Vector;

/**
 * <p>SessionListenerContainerImpl class.</p>
 *
 * @see Wagon
 * @see SessionListener
 * @see SessionListenerContainer
 * @author jay
 * @version $Id: $Id
 */
public class SessionListenerContainerImpl implements SessionListenerContainer {

    private final Wagon wagon;
    private final Vector<SessionListener> sessionListeners;

    /**
     * <p>Constructor for SessionListenerContainerImpl.</p>
     *
     * @param wagon a {@link org.apache.maven.wagon.Wagon} object.
     */
    public SessionListenerContainerImpl(Wagon wagon) {
        this.wagon = wagon;
        sessionListeners = new Vector<>();
    }

    /** {@inheritDoc} */
    @Override
    public void addSessionListener(SessionListener sessionListener) {
        if (sessionListener == null) {
            throw new NullPointerException();
        }
        if (!sessionListeners.contains(sessionListener)) {
            sessionListeners.add(sessionListener);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeSessionListener(SessionListener sessionListener) {
        sessionListeners.remove(sessionListener);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasSessionListener(SessionListener sessionListener) {
        return sessionListeners.contains(sessionListener);
    }

    /**
     * {@inheritDoc}
     *
     * void fireSessionOpening
     */
    @Override
    public void fireSessionOpening() {
        SessionEvent sessionEvent = new SessionEvent(this.wagon, SessionEvent.SESSION_OPENING);
        sessionListeners.forEach(e -> e.sessionOpening(sessionEvent));
    }

    /**
     * {@inheritDoc}
     *
     * void fireSessionOpened
     */
    @Override
    public void fireSessionOpened() {
        SessionEvent sessionEvent = new SessionEvent(this.wagon, SessionEvent.SESSION_OPENED);
        sessionListeners.forEach(e -> e.sessionOpened(sessionEvent));
    }

    /**
     * {@inheritDoc}
     *
     * void fireSessionDisconnecting
     */
    @Override
    public void fireSessionDisconnecting() {
        SessionEvent sessionEvent = new SessionEvent(this.wagon, SessionEvent.SESSION_DISCONNECTING);
        sessionListeners.forEach(e -> e.sessionDisconnecting(sessionEvent));
    }

    /**
     * {@inheritDoc}
     *
     * void fireSessionDisconnected
     */
    @Override
    public void fireSessionDisconnected() {
        SessionEvent sessionEvent = new SessionEvent(this.wagon, SessionEvent.SESSION_DISCONNECTED);
        sessionListeners.forEach(se -> se.sessionDisconnected(sessionEvent));
    }

    /**
     * {@inheritDoc}
     *
     * void fireSessionLoggedIn
     */
    @Override
    public void fireSessionLoggedIn() {
        SessionEvent sessionEvent = new SessionEvent(this.wagon, SessionEvent.SESSION_LOGGED_IN);
        sessionListeners.forEach(se -> se.sessionLoggedIn(sessionEvent));
    }

    /**
     * {@inheritDoc}
     *
     * void fireSessionLoggedOff
     */
    @Override
    public void fireSessionLoggedOff() {
        SessionEvent sessionEvent = new SessionEvent(this.wagon, SessionEvent.SESSION_LOGGED_OFF);
        sessionListeners.forEach(se -> se.sessionLoggedOff(sessionEvent));
    }
}
