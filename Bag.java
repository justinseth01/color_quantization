import java.util.Iterator;

public class Bag implements Iterable<Pixel>
  {
     private Node first;  // first node in list
     private int size = 0;

     private class Node
     {
        Pixel item;
        Node next; 
    }

     public void add(Pixel pixel)
     {  // same as push() in Stack
        Node oldfirst = first;
        first = new Node();
        first.item = pixel;
        first.next = oldfirst;
        size++;
    }
    public boolean isEmpty(){
        return first==null;
    }

    public int size(){
        return size;
    }

    public boolean contains(Pixel pixel){
        Node curr = first;

        while(curr != null){
            if(curr.item.equals(pixel)) return true;
            curr = curr.next;
        }
        return false;
    }
    
     public Iterator<Pixel> iterator(){  
        return new ListIterator(); 
    }

     private class ListIterator implements Iterator<Pixel>
     {
         private Node current = first;
         public boolean hasNext() {  
            return current != null; 
         }
         public void remove() 
         { 

         }
         public Pixel next(){
            Pixel item = current.item;
            current = current.next;
            return item;
        } 
    }
}