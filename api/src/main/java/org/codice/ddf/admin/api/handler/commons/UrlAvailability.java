/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.api.handler.commons;

import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CERT_ERROR;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.UNKNOWN_ENDPOINT;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.UNTRUSTED_CA;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.VERIFIED_URL;

public class UrlAvailability {


    private String url;
    private boolean available;
    private boolean trustedCertAuthority;
    private boolean certError;

    public UrlAvailability(String url) {
        this.url = url;
        available = false;
        trustedCertAuthority = false;
        certError = false;
    }

    public String getUrl() {
        return url;
    }

    public UrlAvailability url(String url) {
        this.url = url;
        return this;
    }

    //Determined whether the endpoint is of the specified type
    public boolean isAvailable() {
        return available;
    }

    public UrlAvailability available(boolean available) {
        this.available = available;
        return this;
    }

    public boolean isTrustedCertAuthority() {
        return trustedCertAuthority;
    }

    public UrlAvailability trustedCertAuthority(boolean trustedCertAuthority) {
        this.trustedCertAuthority = trustedCertAuthority;
        return this;
    }

    public boolean isCertError() {
        return certError;
    }

    public UrlAvailability certError(boolean certError) {
        this.certError = certError;
        return this;
    }

    public String getAvailabilityResult() {
        if(certError) {
            return CERT_ERROR;
        } else if(!trustedCertAuthority) {
            return UNTRUSTED_CA;
        } else if(!available) {
            return UNKNOWN_ENDPOINT;
        } else {
            return VERIFIED_URL;
        }
    }
}
