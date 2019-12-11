/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author wmao
 */
public class JsonDateSerializer extends JsonSerializer<Date> {  
   private static final SimpleDateFormat DF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
   @Override  
   public void serialize(Date date, JsonGenerator gen, SerializerProvider provider)  throws IOException, JsonProcessingException {  
       String value = DF.format(date);  
       gen.writeString(value);  
   }  
}  