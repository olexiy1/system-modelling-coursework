package bank_model;

import java.util.ArrayList;
import java.util.Random;

public class NextElementDecider {
    public Element ChooseElement(ArrayList<NextElement> nextElements, Random RNG){
        //якщо всього 1 елемент то обираємо завжди його
        if(nextElements.size() == 1){
            return nextElements.get(0).getElement();
        }
        ArrayList<NextElement> priorElements = new ArrayList<NextElement>();
        ArrayList<NextElement> probElements = new ArrayList<NextElement>();
        for(NextElement ne : nextElements){
            boolean hasPriority = ne.getPriority() >= 0;
            boolean hasProbability = ne.getProbability() > 0;

            if (hasPriority == hasProbability) {
                throw new IllegalStateException(
                        "Element must have either priority or probability, but not both"
                );
            }
            else if (hasPriority) {
                priorElements.add(ne);
            }
            else{
                probElements.add(ne);
            }
        }
        if(!priorElements.isEmpty() && !probElements.isEmpty()){
            throw new IllegalStateException("Set of elements cannot have both probabilities and priorities");
        }
        else if(!priorElements.isEmpty()){
            return ChooseByPriority(priorElements);
        }
        else{
            return ChooseByProbability(probElements, RNG);
        }
    }

    public Element ChooseByPriority(ArrayList<NextElement> nextElements){
        if (nextElements.size() == 1) {
            return nextElements.get(0).getElement();
        }
        Element selectedElement=null;
        int maxPriorValue=Integer.MAX_VALUE; //чим менше значення пріоритету тим вищий проіритет
        for(NextElement ne: nextElements){
            if(ne.getPriority()<0){
                throw new IllegalArgumentException("Negative priority");
            }
            if(maxPriorValue > ne.getPriority()){
                selectedElement = ne.getElement();
                maxPriorValue = ne.getPriority();
            }

        }
        if(selectedElement == null)
            throw new IllegalStateException("Priorities were not set correctly!");
        return selectedElement;
    }

    public Element ChooseByProbability(ArrayList<NextElement> nextElements, Random RNG){
        if (nextElements.size() == 1) {
            return nextElements.get(0).getElement();
        }
        double r = RNG.nextDouble();
        Element selectedElement;
        double accumSum = 0;
        for(NextElement ne : nextElements){
            accumSum += ne.getProbability();
        }
        if (Math.abs(accumSum - 1.0) > 1e-9){
            throw new IllegalArgumentException("Probabilities must sum up to 1");
        }
        accumSum = 0;
        for(NextElement ne : nextElements){
            if (ne.getProbability() < 0) {
                throw new IllegalArgumentException("Negative probability");
            }
            accumSum += ne.getProbability();
            if(r < accumSum) {
                selectedElement = ne.getElement();
                return selectedElement;
            }
        }
        throw new IllegalStateException("Probabilities were not set correctly!");
    }
}
