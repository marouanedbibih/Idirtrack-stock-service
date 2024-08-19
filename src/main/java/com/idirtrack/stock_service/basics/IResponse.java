package com.idirtrack.stock_service.basics;

import org.apache.hc.core5.http.HttpStatus;

public interface IResponse {
    
        public HttpStatus getStatus();
    
        public MessageType getMessageType();
    
        public String getMessage();
    
        public Object getData();
    
        public void setStatus(HttpStatus status);
    
        public void setMessageType(MessageType messageType);
    
        public void setMessage(String message);
    
        public void setData(Object data);
}
