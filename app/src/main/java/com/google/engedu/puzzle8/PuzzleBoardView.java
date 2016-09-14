package com.google.engedu.puzzle8;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class PuzzleBoardView extends View {
    public static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    public void initialize(Bitmap imageBitmap) {
        int width = getWidth();
        puzzleBoard = new PuzzleBoard(imageBitmap, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }

    public void shuffle() {
        if (animation == null && puzzleBoard != null) {
            // Do something. Then:
            ArrayList<PuzzleBoard> list=new ArrayList<>();
            Random random = new Random();
            Random random2 = new Random();
            int nOfShuffle = 5 + random.nextInt(5);
            for(int i=0;i<nOfShuffle;i++)
            {
                list = puzzleBoard.neighbours();
                puzzleBoard = list.get(random2.nextInt(list.size()));
            }
            puzzleBoard.reset();
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Congratulations!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    Comparator<PuzzleBoard> puzzleBoardComparator = new Comparator<PuzzleBoard>() {
        @Override
        public int compare(PuzzleBoard lhs, PuzzleBoard rhs) {
            return Integer.valueOf(lhs.priority()).compareTo(rhs.priority());
        }
    };

    public void solve() {
        PriorityQueue<PuzzleBoard> Q =  new PriorityQueue(11,puzzleBoardComparator);
        Q.add(puzzleBoard);
        while(!puzzleBoard.resolved())
        {
            ArrayList<PuzzleBoard> nb = puzzleBoard.neighbours();
            for(int j=0;j<nb.size();j++)
            {
                if(Q.contains(nb.get(j)))
                    Q.add(nb.get(j));
            }
            puzzleBoard=Q.poll();
        }

        PuzzleBoard pb = new PuzzleBoard(puzzleBoard);
        while(pb!=null)
        {
            animation.add(pb);
            pb = pb.parent;
        }
        Collections.reverse(animation);

       invalidate();
    }
}
