/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.databridge.core.internal.authentication;

import org.wso2.carbon.identity.authentication.AuthenticationService;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

/**
 * CarbonAuthenticationHandler implementation that authenticate Agents
 * via Carbon AuthenticationService
 */
public class CarbonAuthenticationHandler implements AuthenticationHandler {
    private AuthenticationService authenticationService;

    public CarbonAuthenticationHandler(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public boolean authenticate(String userName, String password) {
        return authenticationService.authenticate(userName, password);
    }

    @Override
    public String getTenantDomain(String userName) {
      return   MultitenantUtils.getTenantDomain(userName);
    }


}
