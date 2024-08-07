package com.idirtrack.stock_service.basics;

import lombok.Builder;
import lombok.Data;

@Data

public class BasicException extends Exception{
  private BasicResponse response;

  public BasicException(BasicResponse response){
    super(response.getMessage());
    this.response = response;
  }
  public BasicException(String message){
    super(message);
  }
}
