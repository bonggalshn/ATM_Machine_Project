package com.switching.service;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class BankMappingService {

    public String setCharge(String bankCode, ISOMsg isoMessage){
        int charge;
        try{
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);
            isoMessage.setPackager(packager);
            byte[] result;

            switch (bankCode){
                case "002":
                    charge = 6500;
                    break;
                default:
                    charge = 6500;
                    break;
            }

            System.out.println("Give charge: "+charge);
            isoMessage.set(4, (Integer.parseInt(isoMessage.getString(4))+charge)+"");
            result = isoMessage.pack();
            return new String(result);

        }catch (Exception e){
            System.out.println("Bank Mapping Service.");
            System.out.println("setCharge.\n"+e.getMessage());
            return "BMS.setCharge: "+e.getMessage();
        }
    }

    public String getUrlInquiry(String bankCode){
        try{
            switch (bankCode){
                case "002":
                    System.out.println("Bank code : 002");
                    return "http://localhost:8081/transfer/ReceiveExternalInquiry";
                default:
                    return "404";
            }
        }catch (Exception e){
            System.out.println("Bank Mapping Service.");
            System.out.println("Get Url.\n"+e.getMessage());
            return "BMS.getUrl: "+e.getMessage();
        }
    }

    public String getUrlTransfer(String bankCode){
        try{
            switch (bankCode){
                case "002":
                    System.out.println("Bank code : 002");
                    return "http://localhost:8081/transfer/ReceiveExternalTransfer";
                default:
                    return "404";
            }
        }catch (Exception e){
            System.out.println("Bank Mapping Service.");
            System.out.println("Get Url.\n"+e.getMessage());
            return "BMS.getUrl: "+e.getMessage();
        }
    }

}
