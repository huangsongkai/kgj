package com.example.demo.config;

import org.springframework.stereotype.Component;

/**
 * Created by song on 2017/11/8.
 */
@Component
//@RepositoryEventHandler(NeedsOwner.class)
public class PurchaseRecordHandler {
//    static {
//        System.out.println("--------------- scan PurchaseRecordHandler");
//    }
//
////    @HandleBeforeSave
//    @HandleBeforeCreate
//    public void handlePurchaseRecordSave(NeedsOwner p) {
//        // … you can now deal with Person in a type-safe way
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username = "";
//        if (principal instanceof UserDetails) {
//            username = ((UserDetails)principal).getUsername();
//        } else {
//            username = principal.toString();
//        }
//        if (username.equals("anonymousUser")){
//            username = "swl";
//        }
//        p.setUsername(username);
//    }
}
