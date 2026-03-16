package bank_model;

public class NextElement {
    private final Element element;
    private final double probability;
    private final int priority;

    public NextElement(Element element){
        this.element = element;
        probability = 0;
        priority = -1;
    }

    public NextElement(Element element, double probability) {
        this.element = element;
        this.probability = probability;
        this.priority = -1;
    }
    public NextElement(Element element, int priority){
        this.element = element;
        this.priority = priority;
        this.probability = 0;
    }

    public Element getElement() {
        return element;
    }

    public double getProbability() {
        return probability;
    }
    public int getPriority() {
        return priority;
    }
}
