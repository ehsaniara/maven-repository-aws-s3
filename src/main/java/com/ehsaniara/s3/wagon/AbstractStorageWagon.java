/*
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

package com.ehsaniara.s3.wagon;

import com.ehsaniara.s3.listener.SessionListenerContainer;
import com.ehsaniara.s3.listener.SessionListenerContainerImpl;
import com.ehsaniara.s3.listener.TransferListenerContainer;
import com.ehsaniara.s3.listener.TransferListenerContainerImpl;
import com.ehsaniara.s3.resolver.BaseDirectoryResolver;
import com.ehsaniara.s3.resolver.BucketResolver;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.events.SessionListener;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import org.apache.maven.wagon.repository.Repository;

import java.util.logging.Logger;

public abstract class AbstractStorageWagon implements Wagon {

    private static final boolean SUPPORTS_DIRECTORY_COPY = true;

    private int connectionTimeOut = 0;
    private int readConnectionTimeOut = 0;

    protected Repository repository = null;

    protected final BucketResolver accountResolver;
    protected final BaseDirectoryResolver containerResolver;

    protected final SessionListenerContainer sessionListenerContainer;
    protected final TransferListenerContainer transferListenerContainer;

    private boolean interactive;

    private static final Logger LOGGER = Logger.getLogger(AbstractStorageWagon.class.getName());

    public AbstractStorageWagon() {
        this.accountResolver = new BucketResolver();
        this.containerResolver = new BaseDirectoryResolver();
        this.sessionListenerContainer = new SessionListenerContainerImpl(this);
        this.transferListenerContainer = new TransferListenerContainerImpl(this);
    }

    @Override
    public boolean supportsDirectoryCopy() {
        return SUPPORTS_DIRECTORY_COPY;
    }

    @Override
    public Repository getRepository() {
        return repository;
    }

    @Override
    public void openConnection() throws ConnectionException, AuthenticationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void connect(Repository repository) throws ConnectionException, AuthenticationException {
        connect(repository, null, (ProxyInfoProvider) null);
    }

    @Override
    public void connect(Repository repository, ProxyInfo proxyInfo) throws ConnectionException, AuthenticationException {
        connect(repository, null, proxyInfo);
    }

    @Override
    public void connect(Repository repository, ProxyInfoProvider proxyInfoProvider) throws ConnectionException, AuthenticationException {
        connect(repository, null, proxyInfoProvider);
    }

    @Override
    public void connect(Repository repository, AuthenticationInfo authenticationInfo) throws ConnectionException, AuthenticationException {
        connect(repository, authenticationInfo, (ProxyInfoProvider) null);
    }

    @Override
    public void connect(Repository repository, AuthenticationInfo authenticationInfo, ProxyInfo proxyInfo) throws ConnectionException, AuthenticationException {
        connect(repository, authenticationInfo, p -> {
            if ((p == null) || (proxyInfo == null) || p.equalsIgnoreCase(proxyInfo.getType())) return proxyInfo;
            else return null;
        });
    }

    @Override
    public void setTimeout(int i) {
        this.connectionTimeOut = i;
    }

    @Override
    public int getTimeout() {
        return connectionTimeOut;
    }

    @Override
    public void setReadTimeout(int i) {
        readConnectionTimeOut = i;
    }

    @Override
    public int getReadTimeout() {
        return readConnectionTimeOut;
    }

    @Override
    public void addSessionListener(SessionListener sessionListener) {
        sessionListenerContainer.addSessionListener(sessionListener);
    }

    @Override
    public void removeSessionListener(SessionListener sessionListener) {
        sessionListenerContainer.removeSessionListener(sessionListener);
    }

    @Override
    public boolean hasSessionListener(SessionListener sessionListener) {
        return sessionListenerContainer.hasSessionListener(sessionListener);
    }

    @Override
    public void addTransferListener(TransferListener transferListener) {
        transferListenerContainer.addTransferListener(transferListener);
    }

    @Override
    public void removeTransferListener(TransferListener transferListener) {
        transferListenerContainer.removeTransferListener(transferListener);
    }

    @Override
    public boolean hasTransferListener(TransferListener transferListener) {
        return transferListenerContainer.hasTransferListener(transferListener);
    }

    @Override
    public boolean isInteractive() {
        return interactive;
    }

    @Override
    public void setInteractive(boolean b) {
        interactive = b;
    }

}
