/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.drugorders.extension.html;

/**
 * This class defines the links that will appear on the administration page under the
 * "drugorders.title" heading. 
 */
import java.util.LinkedHashMap;
import java.util.Map;
import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;


public class AdminList extends AdministrationSectionExt {
	
	/**
         * @return 
	 * @see AdministrationSectionExt#getMediaType()
	 */
        @Override
	public Extension.MEDIA_TYPE getMediaType() {
            return Extension.MEDIA_TYPE.html;
	}
	
	/**
         * @return 
	 * @see AdministrationSectionExt#getTitle()
	 */
        @Override
	public String getTitle() {
            return "drugorders.title";
	}
	
	/**
         * @return 
	 * @see AdministrationSectionExt#getLinks()
	 */
        @Override
	public Map<String, String> getLinks() {
        	
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put("/pages/drugorders/administration.page", "drugorders.administration");
            return map;
	}
	
}
