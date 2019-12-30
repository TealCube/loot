package info.faceland.loot.data;

public class PriceData {

  private int price;
  private boolean rare;

  public PriceData (int price, boolean rare) {
    this.price = price;
    this.rare = rare;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public boolean isRare() {
    return rare;
  }

  public void setRare(boolean rare) {
    this.rare = rare;
  }

}
