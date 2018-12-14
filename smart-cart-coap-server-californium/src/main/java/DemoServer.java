/*
 * Demo Coap Server for smart cart
 * 2018
 * Alexandros Tsichouridis
 *  
 * 
 * 
 * */

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.BAD_REQUEST;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CHANGED;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.DELETED;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class DemoServer {
	
	private static final Logger LOGGER = Logger.getLogger(DemoServer.class.getName());

	
	public static void main(String[] args) {
		
		// binds on UDP port 5683
		CoapServer server = new CoapServer();
		
		server.add(new HelloResource());
		
		server.add(new PriceResource());
		
		server.start();
		
	}
	
	public static class HelloResource extends CoapResource {
		
		public HelloResource() {
			
			super("Hello");
			
			getAttributes().setTitle("Hello-World resource");
			
		}

		@Override
		public void handleGET(CoapExchange exchange) {
			exchange.respond("hello \ncustomer!");
		}
		
	}
	
	public static class PriceResource extends CoapResource{
		
		private Set<Product> cart;
		private List<Product> productList;
		double totalAmount;
		
		public PriceResource() {
			super("price");
			cart = new HashSet<Product>();
			productList = new ArrayList<Product>(); 
			productList.add(new Product("13017258",1,"Sapouni",4.1));
			productList.add(new Product("13135133",3,"cheese",2.2));
			productList.add(new Product("13130599",2,"chocolate Lacta 250g",1.7));
			productList.add(new Product("12948639",4,"Milk 1L",0.9));
			productList.add(new Product("15571198",5,"Psomi tou tost",2.2));
			productList.add(new Product("15590689",6,"Odontokrema (1+1)",3.5));
			productList.add(new Product("15610209",7,"Avga 10 tmx", 1.99));
			productList.add(new Product("15631553",8,"Alevri 1kg", 1.26));
			productList.add(new Product("15612012",9,"Merenda",3.5));
			totalAmount = 0;
		}

		@Override
		public void handleGET(CoapExchange exchange) {
			// TODO Auto-generated method stub
			super.handleGET(exchange);
		}
		
		private void calculateTotalAmount() {
			double amount = 0;
			for(Product p : cart) {
				amount += p.getProductPrice();
			}
			totalAmount = amount;
		}

		@Override
		public void handlePOST(CoapExchange exchange) {
			LOGGER.info(exchange.getRequestCode().toString());
			System.out.println(exchange.getRequestPayload());
			String productCode  = "";
			try {
				productCode = new String(exchange.getRequestPayload(),0,8, "UTF-8");
				System.out.println(productCode);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(Product p : productList) {
				if (productCode.equals(p.productCode)) {
					cart.add(p);
					calculateTotalAmount();
					System.out.println("Product detected: " + p.getProductName());
					System.out.println("Total price: " + totalAmount);
					break;
				}
			}
			exchange.respond(Double.toString(totalAmount));
		}
		
		class Cart {
			
			private List<CartItem> items;
			private double totalAmount;
			
			
			
			public Cart(List<CartItem> items) {
				super();
				this.items = items;
				this.totalAmount = 0;
			}



			public double getTotalAmount() {
				return totalAmount;
			}



			public void setTotalAmount(double totalAmount) {
				this.totalAmount = totalAmount;
			}



			public List<CartItem> getItems() {
				return items;
			}



			public int addItem(Product p) {
				
				for (CartItem item : items) {
					if(item.product.equals(p)) return -1;
				}
				items.add(new CartItem(p, new Timestamp(System.currentTimeMillis())));
				this.totalAmount += p.productPrice;
				return 1;
			}
			
			// add a remove item method...
			
			
		}
		
		class CartItem {
			
			private Product product;
			private Timestamp timestamp;
			
			
			public CartItem(Product product, Timestamp timestamp) {
				super();
				this.product = product;
				this.timestamp = timestamp;
			}
			public Product getProduct() {
				return product;
			}
			public void setProduct(Product product) {
				this.product = product;
			}
			public Timestamp getTimestamp() {
				return timestamp;
			}
			public void setTimestamp(Timestamp timestamp) {
				this.timestamp = timestamp;
			}
			
			
		}
		
		class Product {
			
			private String productCode;
			private int productId;
			private String productName;
			private double productPrice;
			private Timestamp timestamp; 
			
			
			public Product(String productCode, int productId, String productName, double productPrice) {
				super();
				this.productCode = productCode;
				this.productId = productId;
				this.productName = productName;
				this.productPrice = productPrice;
				this.timestamp = new Timestamp(System.currentTimeMillis());
			}
			
			public Product() {};
			
			public String getProductCode() {
				return productCode;
			}
			public void setProductCode(String productCode) {
				this.productCode = productCode;
			}
			public int getProductId() {
				return productId;
			}
			public void setProductId(int productId) {
				this.productId = productId;
			}
			public String getProductName() {
				return productName;
			}
			public void setProductName(String productName) {
				this.productName = productName;
			}
			public double getProductPrice() {
				return productPrice;
			}
			public void setProductPrice(double productPrice) {
				this.productPrice = productPrice;
			}
			
			
		}
		
	}

}
