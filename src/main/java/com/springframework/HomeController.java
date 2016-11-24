package com.springframework;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
public class HomeController {
	
	private static final Logger LOGGER = Logger.getLogger( HomeController.class );
	
	@RequestMapping("/")
	public String home(){
		return "home";
	}
	
	
	@RequestMapping("/api/application/{name}")
	public void homeName(@PathVariable("name") String name, HttpServletResponse response) throws IOException{
				
				response.setContentType("text");
				PrintWriter out = response.getWriter();
				out.println("Hello " + name);
		}
	
	@RequestMapping(value = "/api/application/", method = RequestMethod.POST)
	public @ResponseBody String jsonData(@RequestBody String body, HttpServletRequest request) throws IOException, JSONFormatIsNotCorrectException{
		ObjectMapper objectMapper = new ObjectMapper();
		StringBuilder sb = new StringBuilder();
		JsonNode node = null;		
		
		try
		{
			node = objectMapper.readTree(body);
		} catch (IOException e){
			LOGGER.error("IO Exception", e);
			throw new JSONFormatIsNotCorrectException("Wrong Json");
		}
		
		sb.append("hello ");
		sb.append(node.get("name").textValue());
				
		return sb.toString();
	}
	
	@RequestMapping(value = "/api/application1/", method = RequestMethod.POST)
	public @ResponseBody String jsonArray( @RequestBody String body, HttpServletRequest request) throws JSONFormatIsNotCorrectException, WordPartMissingException, WordIsEmptyException, OrderNumberPartMissingException {
		
		
		ObjectMapper objectMapper = new ObjectMapper();
		StringBuilder sb = new StringBuilder();
		TreeMap map = new TreeMap<Integer, String>();
		JsonNode node = null;	
		
			try {
				node = objectMapper.readTree(body);
			} catch (IOException e) {
				LOGGER.error("Wrong JSON", e);
				throw new JSONFormatIsNotCorrectException("Wrong JSON");
			}
			
			
			
			for(JsonNode root : node){
				validate(root);
			}
			
			for(JsonNode root : node){
				map.put(root.get("orderNumber").asInt(), root.get("word").asText());
			}
			
			
		int size = map.size();
		for(int i = 1; i<=size; i++){
			if(i!=size)	{		
				sb.append((String) map.get(i) + " ");
			} else {
				sb.append((String) map.get(i));
			}
		}
		
		return sb.toString();
	
	}
	
	private void validate(JsonNode root) throws WordPartMissingException, WordIsEmptyException, OrderNumberPartMissingException {
		if(!root.has("word")){
			throw new WordPartMissingException("Word part missing in JSON");
		}
		if(root.get("word").asText().isEmpty()){
			throw new WordIsEmptyException("Word is empty");
		}
		if(!root.has("orderNumber")){
			throw new OrderNumberPartMissingException("OrderNumber part missing in JSON");
		}

		
	}

	@ExceptionHandler(value = WordPartMissingException.class)
	public @ResponseBody String handleWordPartMisiingException(Exception e){
		
		return "Word part is missing";
	}
	
	@ExceptionHandler(value = WordIsEmptyException.class)
	public @ResponseBody String handleWordIsEmptyException(Exception e){
		return "Word is empty";
	}
	
	@ExceptionHandler(value = JSONFormatIsNotCorrectException.class)
	public @ResponseBody String handleJSONFormatIsNotCorrectException(Exception e){
		
		return "JSON Format is wrong";
	}
	
	@ExceptionHandler(value = OrderNumberPartMissingException.class)
	public @ResponseBody String handleOrderNumberPartMisiingException(Exception e){
		
		return "OrderNumber part is missing";
	}
	
	
}
