import java.awt.*;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CrossRaceGame extends JPanel{

    
    //variables used in the game's working like location and speed of the car in x & y coordinates, number of oponents, input the score, input key values etc
    String imageLoc[]; 
    int lx[],ly[];  //oponent or incoming vehicle's location
    int score;         
    int speedOfTheOpponent[]; 
    boolean isGameFinished; 
    boolean isUp, isDown, isRight, isLeft;  
    int crx,cry;	//crossing location
    int car_x,car_y;    //car location
    int speedX,speedY;	//car speed
    int numberOfOpponent;      
    
    
   


    public CrossRaceGame(){
        //initialing the location of the crossing as -999 for both x & y coordinates
        crx = cry = -999;   
        //adding a listener to get input from user when a key is pressed and released. The car will stop the motion when key is released
        addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }
            public void keyReleased(KeyEvent e) { 
                stopCar(e); 
            }
            public void keyPressed(KeyEvent e) { 
                moveCar(e); 
            }
        });


        setFocusable(true); //indicating that our Jpanel is focusable
        //initialising our other variables
        car_x = car_y = 300;    
        isUp = isDown = isLeft = isRight = false;   
        speedX = speedY = 0;    
        numberOfOpponent = 0;  
        lx = new int[20]; 
        ly = new int[20]; 
        imageLoc = new String[20];
        speedOfTheOpponent = new int[20]; 
        isGameFinished = false; 
        score  = 0;  
    }
    
    //in this part of the code the function would paint all graphic images to the screen at specified locations
    //and the scene is repainted everytime the scene settings change
    public void paint(Graphics g){
        super.paint(g);
        Graphics2D obj = (Graphics2D) g;
        obj.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        try{
            obj.drawImage(getToolkit().getImage("images/st_road.png"), 0, 0 ,this); //paints the straight road image
            if(cry >= -499 && crx >= -499) 
                obj.drawImage(getToolkit().getImage("images/cross_road.png"),crx,cry,this); //paints the crossroad image
            
            obj.drawImage(getToolkit().getImage("images/car_self.png"),car_x,car_y,this);   //paints user's car
            
            if(isGameFinished){ 
                obj.drawImage(getToolkit().getImage("images/boom.png"),car_x-30,car_y-30,this); //paints the boom image when collision happens/when game is over
            }
            
            if(this.numberOfOpponent > 0){ 
                for(int i=0;i<this.numberOfOpponent;i++){ 
                    obj.drawImage(getToolkit().getImage(this.imageLoc[i]),this.lx[i],this.ly[i],this); //paints opponent car's image
                }
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        
    }
    
    //this fuction very efficiently moves the road scene so that it gives an impression that the car is moving
    void moveRoad(int count){
        if(crx == -999 && cry == -999){ 
            //if the road crossing is passed then sends the crossing back to origin after a certain point of time 
            if(count%10 == 0){  
                
                crx = 499;      
                cry = 0;
            }
        }
        else{   
            crx--; //otherwise it keeps moving across the frame
        }

        //if the car crosses without collision
        if(crx == -499 && cry == 0){ 
            crx = cry = -999;   
        }
        //updates incoming car's position
        car_x += speedX; 
        car_y += speedY; 
        
        //following are certain cases where I tested alot to get the analysis for each it restricts the car from going 
        //outside the left or right side of the road by keeping a check on it's min & max x and y axis value


        if(car_x < 0)   
            car_x = 0;  
        
        
        if(car_x+93 >= 500) 
            car_x = 500-93; 
        
        if(car_y <= 124)    
            car_y = 124;    
        
        
        if(car_y >= 364-50) 
            car_y = 364-50; 
        
        //moves the oponents car accoring to the values calculated from above(these values are set according to best estimate)
        for(int i=0;i<this.numberOfOpponent;i++){ 
            this.lx[i] -= speedOfTheOpponent[i]; 
        }
        
        
        int index[] = new int[numberOfOpponent];
        for(int i=0;i<numberOfOpponent;i++){
            if(lx[i] >= -127){
                index[i] = 1;
            }
        }
        int c = 0;
        for(int i=0;i<numberOfOpponent;i++){
            if(index[i] == 1){
                imageLoc[c] = imageLoc[i];
                lx[c] = lx[i];
                ly[c] = ly[i];
                speedOfTheOpponent[c] = speedOfTheOpponent[i];
                c++;
            }
        }
        
        //calulating the score by counting the number of cars it has passed

        score += numberOfOpponent - c; 
        
 
        
            numberOfOpponent = c;
        
        //checking for endgame or collision
        int diff = 0; 
        for(int i=0;i<numberOfOpponent;i++){ 
            diff = car_y - ly[i]; 
            if((ly[i] >= car_y && ly[i] <= car_y+46) || (ly[i]+46 >= car_y && ly[i]+46 <= car_y+46)){  
                if(car_x+87 >= lx[i] && !(car_x >= lx[i]+87)){  
                    System.out.println("User's car : "+car_x+", "+car_y);
                    System.out.println("Oponent's Colliding car : "+lx[i]+", "+ly[i]);
                    this.finish(); 
                }
            }
        }
    }
    
    //display at the end of collision/Game ending 
    void finish(){
        String str = "";    
        isGameFinished = true;  
        this.repaint();     
        if(score == 0) 
            str = "\nHard luck you lost at duck! Try again ?";  
        if(score > 0) 
            str = "\nNot bad wanna go again ?";  //create a try again message
        JOptionPane.showMessageDialog(this,"Game Over Racer!! \nYour Score : "+score+str,     "Game Over", JOptionPane.YES_NO_OPTION);    //displays the congratulations message and a message saying game over and the users score and the high score
        System.exit(ABORT); //terminates game
    }
    
    
    //function that handles input by user to move the user's car up, left, down and right
    public void moveCar(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_UP){   
            isUp = true;
            speedX = 1;     //moves car foward
        }
        if(e.getKeyCode() == KeyEvent.VK_DOWN){ 
            isDown = true;
            speedX = -2;    //moves car backwards
        }
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){ 
            isRight = true;
            speedY = 1;     //moves car to the right
        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT){ 
            isLeft = true;
            speedY = -1;    //moves car to the left
        }
    }
    
   //function that handles user input when the car is supposed to be stopped
    public void stopCar(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_UP){  
            isUp = false;
            speedX = 0; //set speed of car to zero
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN){   
            isDown = false;
            speedX = 0; //set speed of car to zero
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT){    
            isLeft = false;
            speedY = 0; //set speed of car to zero
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT){   
            isRight = false;
            speedY = 0; //set speed of car to zero
        }
    }
    
   
    //Driver Code
    public static void main(String args[]){

        JFrame frame = new JFrame("cRoss-RaCer");  //Creating a New JFrame window in which the game is played
        //creating one instance for the game to be played
        CrossRaceGame game = new CrossRaceGame(); 
        //2D graphic components of the game 
        frame.add(game);		 
        frame.setSize(500,500);  
        frame.setVisible(true);  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int count = 1, c = 1;
        while(true){
            game.moveRoad(count);    //enabling the above function the moves the road
            while(c <= 1){
                game.repaint();     //enables the function defined above that repaints
                try{
                    //waiting time for reapearing 
                    Thread.sleep(5);    
                }
                catch(Exception e){
                    System.out.println(e);
                }
                c++;
            }
            c = 1;
            count++; //incrementing the counter
            if(game.numberOfOpponent < 4 && count % 200 == 0){  
                //assigns images of the incoming car in a random way
                game.imageLoc[game.numberOfOpponent] = "images/car_left_"+((int)((Math.random()*100)%3)+1)+".png";  
                //start position set
                game.lx[game.numberOfOpponent] = 499;  

                //since there are four lanes we are randomly assigning lanes to each incoming oponent car
                int p = (int)(Math.random()*100)%4;  

                //case one where the random remainder is 0, car is placed in 1st lane
                if(p == 0){      
                    p = 250;     
                }
                //case two where the random remainder is 1, car is placed in 2nd lane
                else if(p == 1){  
                    p = 300;     
                }
                //case three where the random remainder is 2, car is placed in 3rd lane
                else if(p == 2){  
                    p = 185;     
                }
                //case four where the random remainder is not any of the above ,car is placed in 4th lane
                else{            
                    p = 130;     
                }
                game.ly[game.numberOfOpponent] = p;  

                //assigning the incoming oponent car a random speed and to take care of a corner case it will be atleast 2
                game.speedOfTheOpponent[game.numberOfOpponent] = (int)(Math.random()*100)%2 + 2;  
                game.numberOfOpponent++;  
            }
        }
    }
}
