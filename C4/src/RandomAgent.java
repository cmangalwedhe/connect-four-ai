//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.Random;

public class RandomAgent extends Agent {
    Random r = new Random();

    public RandomAgent(Connect4Game var1, boolean var2) {
        super(var1, var2);
    }

    public void move() {
        if (!this.myGame.boardFull()) {
            int var1;
            for(var1 = this.r.nextInt(this.myGame.getColumnCount()); this.getTopEmptySlot(this.myGame.getColumn(var1)) == null; var1 = this.r.nextInt(this.myGame.getColumnCount())) {
            }

            this.moveOnColumn(this.myGame, var1);
        }

    }

    public void moveOnColumn(Connect4Game var1, int var2) {
        Connect4Slot var3 = this.getTopEmptySlot(var1.getColumn(var2));
        if (var3 != null) {
            if (this.iAmRed) {
                var3.addRed();
            } else {
                var3.addYellow();
            }
        }

    }

    public Connect4Slot getTopEmptySlot(Connect4Column var1) {
        int var2 = -1;

        for(int var3 = 0; var3 < var1.getRowCount(); ++var3) {
            if (!var1.getSlot(var3).getIsFilled()) {
                var2 = var3;
            }
        }

        if (var2 < 0) {
            return null;
        } else {
            return var1.getSlot(var2);
        }
    }

    public String getName() {
        return "Random Agent";
    }
}
