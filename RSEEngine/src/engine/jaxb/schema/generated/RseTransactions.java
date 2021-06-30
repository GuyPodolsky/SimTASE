//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.03.29 at 04:11:37 PM IDT 
//


package engine.jaxb.schema.generated;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element ref="{}rse-symbol"/>
 *         &lt;element ref="{}rse-company-name"/>
 *         &lt;element ref="{}rse-price"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "rse-transactions")
public class RseTransactions {

    @XmlElement(name = "rse-date-and-time",required = false)
    protected String symbol;
    @XmlElement(name = "rse-date-and-time",required = true)
    protected String rseDateTime;
    @XmlElement(name = "rse-quantity", required = true)
    protected int rseQuantity;
    @XmlElement(name = "rse-price", required = true)
    protected float rsePrice;
    @XmlElement(name = "rse-turn-over", required = true)
    protected float rseTurnover;

    public String getRseBuyer() {
        return rseBuyer;
    }

    public void setRseBuyer(String rseBuyer) {
        this.rseBuyer = rseBuyer;
    }

    public String getRseSeller() {
        return rseSeller;
    }

    public void setRseSeller(String rseSeller) {
        this.rseSeller = rseSeller;
    }

    @XmlElement(name = "rse-buyer")
    protected String rseBuyer;
    @XmlElement(name="rse-seller")
    protected String rseSeller;

    public String getRseDateTime() {
        return rseDateTime;
    }

    public int getRseQuantity() {
        return rseQuantity;
    }

    public float getRsePrice() {
        return rsePrice;
    }

    public float getRseTurnover() {
        return rseTurnover;
    }

    public void setRseDateTime(String rseDateTime) {
        this.rseDateTime = rseDateTime;
    }

    public void setRseQuantity(int rseQuantity) {
        this.rseQuantity = rseQuantity;
    }

    public void setRsePrice(float rsePrice) {
        this.rsePrice = rsePrice;
    }

    public void setRseTurnover(float rseTurnover) {
        this.rseTurnover = rseTurnover;
    }

    public String getRseStock() { return symbol; }
}