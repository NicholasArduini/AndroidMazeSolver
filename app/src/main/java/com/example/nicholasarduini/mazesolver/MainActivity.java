package com.example.nicholasarduini.mazesolver;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    /*
    To add more buttons

    In Layout:
    The code for this maze solver is organized in such a way that to add more rows or columns
    with the proper naming or the button id in the format of btn(column number)x(row number) for
    example btn2x5, this will ensure the new rows and/or columns will get initialized. These buttons
    must also be added to the corresponding linear layouts and in the event of a new row a new linear
    layout should be made.

    In this Code:
    The number of rows and columns variables below must be updated based on the new button layout, since
    the rest of the code requires these variables.
    Also depending on where the new buttons are placed, the border variables below should be updated
    to the new corresponding ids of the border buttons. To get the id of a button use button.getId().
     */

    //if more rows or columns are added to be updated
    private final static int columns = 10;
    private final static int rows = 12;

    //if more rows or columns are added to be updated
    private final static int btmBorderLeftID = 2131493054;
    private final static int btmBorderRightID = 2131493063;
    private final static int topBorderLeftID = 2131492944;
    private final static int topBorderRightID = 2131492953;

    private static Button[][] buttons = new Button[columns][rows];
    private static Button solveBtn;
    private static Button clearBtn;

    private static ArrayList<Integer> wallButtonIDS = new ArrayList<>();
    private static ArrayList<Integer> previousVisitIDS = new ArrayList<>();
    private static ArrayList<Integer> solutionPathIDS = new ArrayList<>();

    private static ArrayList<Integer> leftBorderIDS = new ArrayList();
    private static ArrayList<Integer> rightBorderIDS = new ArrayList();

    private static Boolean startSelected = false;
    private static Boolean finishSelected = false;
    private static Boolean working = false;
    private static int startID = 0;
    private static int finishID = 0;

    private static int recursiveCalls = 0;

    private static Random mRandom = new Random();

    private void delay() {
        try {
            Thread.sleep(mRandom.nextInt(200)+10);
        }
        catch (java.lang.InterruptedException e) {
        }
    }

    private class FindPathTask extends AsyncTask<Void, Integer, Boolean> {

        private boolean recursiveSolve(int id) {
            //reached the end point
            if (id == startID){
                return true;
            }

            //on a wall, or have already been there, or no solution, or id is out of the scope of the borders
            if (wallButtonIDS.contains(id) || previousVisitIDS.contains(id) || recursiveCalls >= 300 || id < topBorderLeftID || id > btmBorderRightID){
                return false;
            }
            previousVisitIDS.add(id);
            recursiveCalls++;

            //if the end point is more than a row away try to move up or down
            //checks to not be on the top border
            if (!(id >= topBorderLeftID && id <= topBorderRightID) && startID < id && id - startID > columns || id - startID < - columns) {
                //call recursive function to move up
                if (recursiveSolve(id - columns)) {
                    solutionPathIDS.add(id);
                    publishProgress(solutionPathIDS.get(solutionPathIDS.size() - 1));
                    delay();
                    return true;
                }
            }

            //checks to not be on the bottom border
            if (!(id >= btmBorderLeftID && id <= btmBorderRightID) && startID > id && id - startID > columns || id - startID < - columns) {
                //call recursive function to move down
                if (recursiveSolve(id + columns)) {
                    solutionPathIDS.add(id);
                    publishProgress(solutionPathIDS.get(solutionPathIDS.size() - 1));
                    delay();
                    return true;
                }
            }


            //checks to not be on the left border
            if (!leftBorderIDS.contains(id) && id > topBorderLeftID) {
                //call recursive function to move left
                if (recursiveSolve(id - 1)) {
                    solutionPathIDS.add(id);
                    publishProgress(solutionPathIDS.get(solutionPathIDS.size() - 1));
                    delay();
                    return true;
                }
            }

            //checks to not be on the right border
            if (!rightBorderIDS.contains(id) && id < btmBorderRightID) {
                //call recursive function to move right
                if (recursiveSolve(id + 1)) {
                    solutionPathIDS.add(id);
                    publishProgress(solutionPathIDS.get(solutionPathIDS.size() - 1));
                    delay();
                    return true;
                }
            }

            //checks to not be on the top border
            if (!(id >= topBorderLeftID && id <= topBorderRightID)) {
                //call recursive function to move up
                if (recursiveSolve(id - columns)) {
                    solutionPathIDS.add(id);
                    publishProgress(solutionPathIDS.get(solutionPathIDS.size() - 1));
                    delay();
                    return true;
                }
            }

            //checks to not be on the bottom border
            if (!(id >= btmBorderLeftID && id <= btmBorderRightID)) {
                //call recursive function to move down
                if (recursiveSolve(id + columns)) {
                    solutionPathIDS.add(id);
                    publishProgress(solutionPathIDS.get(solutionPathIDS.size() - 1));
                    delay();
                    return true;
                }
            }

            return false;
        }


        @Override
        protected Boolean doInBackground(Void... arg0) {
            working = true;
            return recursiveSolve(finishID);

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            //whenever a button is found on the solution path it is coloured blue
            for(int i = 0; i < columns; i++) {
                for (int j = 0; j < rows; j++) {
                    if(buttons[i][j].getId() == progress[0] && startID != progress[0] && finishID != progress[0]) {
                        buttons[i][j].getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            recursiveCalls = 0;
            working = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        solveBtn = (Button) findViewById(R.id.solveBtn);
        clearBtn = (Button) findViewById(R.id.clearBtn);

        //set the right and left borders
        for(int i = 0; i < rows; i++){
            leftBorderIDS.add(topBorderLeftID + columns*i);
            rightBorderIDS.add(topBorderRightID + columns*i);
        }

        //initialize all buttons
        for(int i = 0; i < columns; i++){
            for(int j = 0; j < rows; j++){
                final int resId = getResources().getIdentifier("btn" + (i+1) + "x" + (j+1), "id", getPackageName());
                buttons[i][j] = (Button) findViewById(resId);
                buttons[i][j].getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);

                buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //only works if the solve process isn't already working
                        if (!working) { // selecting buttons
                            if (!startSelected && v.getId() != finishID) { //select start
                                if (wallButtonIDS.contains(v.getId())) { //if button is wall remove wall
                                    wallButtonIDS.remove(wallButtonIDS.indexOf(v.getId()));
                                }
                                v.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                                startID = v.getId();
                                startSelected = true;
                            } else if (startSelected && v.getId() == startID) { //deselect start
                                v.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
                                startID = 0;
                                startSelected = false;
                            } else if (startSelected && !finishSelected && v.getId() != startID) { //select finish
                                if (wallButtonIDS.contains(v.getId())) { //if button is wall remove wall
                                    wallButtonIDS.remove(wallButtonIDS.indexOf(v.getId()));
                                }
                                v.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                                finishID = v.getId();
                                finishSelected = true;
                            } else if (finishSelected && v.getId() == finishID) { //deselect finish
                                v.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
                                finishID = 0;
                                finishSelected = false;
                            } else if (startSelected && finishSelected) {
                                if (wallButtonIDS.contains(v.getId())) { //deselect walls
                                    v.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
                                    wallButtonIDS.remove(wallButtonIDS.indexOf(v.getId()));
                                } else { //select walls
                                    v.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
                                    wallButtonIDS.add(v.getId());

                                }
                            }
                        }
                    }
                });
            }
        }

        solveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recursiveCalls = 0;
                //only works if the solve process isn't already working and change the color of the previous path back to normal
                if (!working) {
                    for (int i = 0; i < columns; i++) {
                        for (int j = 0; j < rows; j++) {
                            if (solutionPathIDS.contains(buttons[i][j].getId()) && !wallButtonIDS.contains(buttons[i][j].getId()) && buttons[i][j].getId() != finishID && buttons[i][j].getId() != startID) {
                                buttons[i][j].getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
                            }
                        }
                    }
                    //clear the previous path data from the array list
                    previousVisitIDS.clear();
                    solutionPathIDS.clear();

                    //call the solve solution from another thread
                    FindPathTask download = new FindPathTask();
                    download.execute();

                }
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recursiveCalls = 0;
                //only works if the solve process isn't already working and changes the color of all the buttons to default
                if (!working) {
                    for (int i = 0; i < columns; i++) {
                        for (int j = 0; j < rows; j++) {
                            buttons[i][j].getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
                        }
                    }

                    //reset the start and finish id's and array lists soring paths and walls
                    startID = 0;
                    finishID = 0;
                    startSelected = false;
                    finishSelected = false;
                    previousVisitIDS.clear();
                    solutionPathIDS.clear();
                    wallButtonIDS.clear();
                }
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //when rotated show the appropriate colors on the buttons for the finish and start id's as well as the walls
        for(int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                    if (wallButtonIDS.contains(buttons[i][j].getId())){
                        buttons[i][j].getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
                    } else if(buttons[i][j].getId() == startID){
                        buttons[i][j].getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                    } else if(buttons[i][j].getId() == finishID){
                        buttons[i][j].getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    }
            }
        }

        //color the solution path blue
        for(int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                if (solutionPathIDS.contains(buttons[i][j].getId()) && buttons[i][j].getId() != finishID && buttons[i][j].getId() != startID) {
                    buttons[i][j].getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
                }
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
