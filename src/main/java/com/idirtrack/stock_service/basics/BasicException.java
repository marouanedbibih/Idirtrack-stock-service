package com.idirtrack.stock_service.basics;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
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
