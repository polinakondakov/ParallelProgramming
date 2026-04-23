package io.mappedbus.sample.object;
import io.mappedbus.MappedBusMessage;
import io.mappedbus.MemoryMappedFile;


public class PriceUpdate implements MappedBusMessage {
	
	public static final int TYPE = 0;

	private int source;
	
	private int price;
	
	private int quantity;
	
	public PriceUpdate() {
	}

	public PriceUpdate(int source, int price, int quantity) {
		this.source = source;
		this.price = price;
		this.quantity = quantity;
	}
	
	public int type() {
		return TYPE;
	}
	
	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int size() {
    return 12;
	}

	@Override
	public String toString() {
		return "PriceUpdate [source=" + source + ", price=" + price + ", quantity=" + quantity + "]";
	}
	
	
	public void write(MemoryMappedFile mem, long pos) { // объект PriceUpdate {source=0, price=20, quantity=40} превращается в байты
		mem.putInt(pos, source);  // пишем source на позицию pos
		mem.putInt(pos + 4, price);   // пишем price через 4 байта после source
		mem.putInt(pos + 8, quantity); // пишем quantity через 8 байтов
	}
	
	public void read(MemoryMappedFile mem, long pos) { // распаковка из памяти в объект
		source = mem.getInt(pos);      // читаем 4 байта с позиции pos → source
		price = mem.getInt(pos + 4);   // читаем 4 байта со сдвигом 4 → price
		quantity = mem.getInt(pos + 8); // читаем 4 байта со сдвигом 8 → quantity	
	}
}