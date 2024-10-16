package com.main.ecommerceprototype.CMS;

import com.main.ecommerceprototype.CMS.Service.CmsService;

import java.net.URL;

// singleton pattern
public class CMS implements CmsService {
    // singleton pattern
    private static CMS instance = null;

    private CMS() {
    }

    public static CMS getInstance() {
        if (instance == null) {
            instance = new CMS();
        }
        return instance;
    }

    @Override
    public URL getPage(PageType pageType) throws IllegalArgumentException {
        return switch (pageType) {
            case HEADER -> getClass().getResource("header.fxml");
            case FOOTER -> getClass().getResource("footer.fxml");
            case LANDING_PAGE -> getClass().getResource("LandingPage.fxml");
            case LANDING_PAGE_CONTAINER_PAGE -> getClass().getResource("landingPageContainer.fxml");
            case PRODUCT_LIST_PAGE -> getClass().getResource("productListPage.fxml");
            case PRODUCT_LIST_PAGE_CONTAINER_PAGE -> getClass().getResource("productListPageContainer.fxml");
            case PRODUCT_DETAIL_PAGE -> getClass().getResource("productDetailPage.fxml");
            case CART_PAGE -> getClass().getResource("cartPage.fxml");
            case CART_CONTAINER_PAGE -> getClass().getResource("cartContainer.fxml");
            case CHECKOUT_PAGE -> getClass().getResource("checkoutPage.fxml");
            case ORDER_CONFIRMATION_PAGE -> getClass().getResource("orderConfirmationPage.fxml");
            case ORDER_HISTORY_CONTAINER_PAGE -> getClass().getResource("orderHistoryContainer.fxml");
            case ORDER_DETAIL_PAGE -> getClass().getResource("orderDetailPage.fxml");
            case ACCOUNT_PAGE -> getClass().getResource("accountPage.fxml");
            case ACCOUNT_DETAILS_CONTAINER_PAGE -> getClass().getResource("accountDetailsContainer.fxml");
            case LOGIN_PAGE -> getClass().getResource("loginPage.fxml");
            case REGISTER_PAGE -> getClass().getResource("registerPage.fxml");
            case RESET_PASSWORD_PAGE -> getClass().getResource("resetPasswordPage.fxml");
            case SET_PASSWORD_PAGE -> getClass().getResource("setPasswordPage.fxml");
            case GUIDE_PAGE -> getClass().getResource("guidePage.fxml");
            case SUPPORT_PAGE -> getClass().getResource("supportPage.fxml");
            default -> throw new IllegalArgumentException("PageType not supported");
        };
    }
}
