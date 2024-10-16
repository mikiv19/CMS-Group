package com.main.ecommerceprototype.CMS.Service;

import com.main.ecommerceprototype.CMS.PageType;

import java.net.URL;

public interface CmsService {

    URL getPage(PageType pageType) throws IllegalArgumentException;
}
