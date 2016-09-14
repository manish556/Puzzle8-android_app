package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;

import java.util.ArrayList;


public class PuzzleBoard {
    private int nOfSteps;
    protected PuzzleBoard parent;
    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        nOfSteps=0;
        parent = null;
        bitmap  = Bitmap.createScaledBitmap(bitmap,parentWidth,parentWidth,false);
        int eachWidth = parentWidth / NUM_TILES;
        int number = -1;
        tiles = new ArrayList<>();
        for(int y=0;y<NUM_TILES;y++)
        {
           for(int x=0;x<NUM_TILES;x++)
           {
               if(!(y==NUM_TILES-1 && x==NUM_TILES-1))
               {
                   Bitmap small = Bitmap.createBitmap(bitmap, x*eachWidth, y*eachWidth, eachWidth, eachWidth);
                   tiles.add(new PuzzleTile(small, ++number));
               }
           }
        }
        tiles.add(null);
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        nOfSteps = otherBoard.nOfSteps+1;
        parent = otherBoard;
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
    }

    public void reset() {
        nOfSteps = 0;
        parent = null;
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    public ArrayList<PuzzleBoard> neighbours() {
        ArrayList<PuzzleBoard> arr = new ArrayList<>();
        int i;
        for(i=0;i<NUM_TILES*NUM_TILES;i++)
            if(tiles.get(i)==null)
                break;
        int C[] = new int [2];
        C[0]=i/NUM_TILES;
        C[1]=i%NUM_TILES;
        int D[] = new int[2];
        for(int j=0;j<4;j++)
        {
            D[0] = C[0]+NEIGHBOUR_COORDS[j][0];
            D[1] = C[1]+NEIGHBOUR_COORDS[j][1];
            if (isValidCord(D))
            {
                PuzzleBoard P = new PuzzleBoard(this);
                P.swapTiles(i,XYtoIndex(D[0],D[1]));
                arr.add(P);
            }
        }
      return arr;
    }

    public boolean isValidCord(int C[])
    {
        if(C[0]<0 || C[0]>=NUM_TILES || C[1]<0 || C[1]>=NUM_TILES)
            return false;
        return true;
    }

    public int priority() {
        int unlocated=0;
        for(int i=0;i<NUM_TILES*NUM_TILES;i++)
        {
            if(tiles.get(i)!=null)
            if(tiles.get(i).getNumber()!=i)
                unlocated++;
        }
        return unlocated+nOfSteps;
    }

}
