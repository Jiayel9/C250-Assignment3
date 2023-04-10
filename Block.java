package assignment3;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Block {
 private int xCoord;
 private int yCoord;
 private int size; // height/width of the square
 private int level; // the root (outermost block) is at level 0
 private int maxDepth;
 private Color color;

 private Block[] children; // {UR, UL, LL, LR}

 public static Random gen = new Random(4);


 /* These two constructors are here for testing purposes.*/
 //test constructor 1
 public Block() {}
 //test constructor 2

 public Block(int x, int y, int size, int lvl, int  maxD, Color c, Block[] subBlocks) {
  this.xCoord=x;
  this.yCoord=y;
  this.size=size;
  this.level=lvl;
  this.maxDepth = maxD;
  this.color=c;
  this.children = subBlocks;
 }


 /* Creates a random block given its level and a max depth.
  * xCoord, yCoord, size, and highlighted should not be initialized
  * (i.e. they will all be initialized by default) */

 public Block(int lvl, int maxDepth) {
  //Just constructor things you forgot about
  this.maxDepth = maxDepth;
  this.level = lvl;

  // Initializing random variable
  double random = 0;

  //If the block can still be subdivided
  if (lvl < maxDepth) {
   random = gen.nextDouble(1);

   // On the condition that you do subdivide the block, colour must be null, children is initialized and filled
   if (random < Math.exp(-0.25 * lvl)) {
    this.color = null;
    this.children = new Block[4];
    for (int i = 0; i < this.children.length; i++) {
     this.children[i] = new Block(lvl+1, maxDepth);
    }
   }
   //if the block is not being subdivided, it will have a colour
   else {
    // leaf condition
    this.children = new Block[0];
    this.color = GameColors.BLOCK_COLORS[gen.nextInt(4)];
   }
  }
  // also a leaf condition
  else if (level == maxDepth) {
   this.children = new Block[0];
   this.color = GameColors.BLOCK_COLORS[gen.nextInt(4)];
  }

 }


 /*
  * Updates size and position for the block and all of its sub-blocks, while
  * ensuring consistency between the attributes and the relationship of the
  * blocks.
  *
  *  The size is the height and width of the block. (xCoord, yCoord) are the
  *  coordinates of the top left corner of the block.
  */
 public void updateSizeAndPosition (int size, int xCoord, int yCoord) {
  //Code here
  // (size % 2 != 0 && this.children.length == 4)
  if (size < 0 || size == 0 || (size % 2 != 0 && size != 1 )) {
   throw new IllegalArgumentException("Size input error");
  }
  //no need for position validation

  this.size = size;
  this.xCoord = xCoord;
  this.yCoord = yCoord;

  if (this.children.length == 4) {
   // Array order: [UR, UL, LL, LR]
   this.children[0].updateSizeAndPosition((this.size/2), (this.xCoord + (this.size / 2)), this.yCoord);
   this.children[1].updateSizeAndPosition((this.size/2), this.xCoord, this.yCoord);
   this.children[2].updateSizeAndPosition((this.size/2), this.xCoord, (this.yCoord + (this.size / 2) ) );
   this.children[3].updateSizeAndPosition((this.size/2), (this.xCoord + (this.size / 2)), (this.yCoord + (this.size / 2)));
  }
 }


 /* Returns a List of blocks to be drawn to get a graphical representation of this block.
  *
  * This includes, for each undivided Block:
  * - one BlockToDraw in the color of the block
  * - another one in the FRAME_COLOR and stroke thickness 3
  *
  * Note that a stroke thickness equal to 0 indicates that the block should be filled with its color.
  *
  * The order in which the blocks to draw appear in the list does NOT matter. */
 public ArrayList<BlockToDraw> getBlocksToDraw() {

  //code here
  ArrayList<BlockToDraw> drawArray = new ArrayList<BlockToDraw>();
  //blockArray.add(new BlockToDraw(null, this.xCoord, this.yCoord, this.size, 3));

  if (children.length == 0 && color != null) {
   drawArray.add(new BlockToDraw(this.color, this.xCoord, this.yCoord, size, 0));
   drawArray.add(new BlockToDraw(GameColors.FRAME_COLOR, this.xCoord, this.yCoord, size, 3));
  }
  else if (children.length == 4 && color == null) {
   for (Block child : children) {
    drawArray.addAll(child.getBlocksToDraw());
   }
  }
  return drawArray;
 }

 // DO NOT TOUCH GETHIGHLIGHTEDFRAME
 public BlockToDraw getHighlightedFrame() {
  return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
 }



 /*
  * Return the Block within this Block that includes the given location
  * and is at the given level. If the level specified is lower than
  * the lowest block at the specified location, then return the block
  * at the location with the closest level value.
  *
  * The location is specified by its (x, y) coordinates. The lvl indicates
  * the level of the desired Block. Note that if a Block includes the location
  * (x, y), and that Block is subdivided, then one of its sub-Blocks will
  * contain the location (x, y) too. This is why we need lvl to identify
  * which Block should be returned.
  *
  * Input validation:
  * - this.level <= lvl <= maxDepth (if not throw exception)
  * - if (x,y) is not within this Block, return null.
  */
 public Block getSelectedBlock(int x, int y, int lvl) {
  //Input validation

  //If the lvl provided is a larger block than the current block or the level is deeper than the max depth
  if (lvl < level || lvl > maxDepth) {
   throw new IllegalArgumentException("The level input is too big or too small");
  }

  //initializing storage variable
  Block sBlock = null;

  // base case, if the block is the block we are looking for
  if ( (xCoord <= x && x <= xCoord + size) && (yCoord <= y && y <= yCoord + size)  && level == lvl) {
   return this;
  }

  // if the block is NOT the block we are looking for
  // BUT the block has children, iterate through the children and recurse
  else if (children.length == 4) {
   for (Block child: children) {

    // Under the condition that the level of the child is greater than the level we are looking for
    // Stop iterating through the children
    if (lvl < child.level) {
     break;
    }

    //Only reassign the value of sBlock IF and only if the child IS the block we are looking for
    if ( (child.getSelectedBlock(x, y, lvl)) != null){
     sBlock = child.getSelectedBlock(x, y, lvl);
     break;
    }
   }
  }

  // Provided that: This block is not the block we are looking for
  // AND this block does not have children, return null
  // At the end sBlock should be found or just null
  return sBlock;
 }

private void xreflect() {
 // Reminder that for x axis reflect the y value changes

 if (children.length == 4) {
    Block[] bArr = new Block[4];
    Block z = children[0];
    Block o = children[1];
    Block t = children[2];
    Block th = children[3];

    z.updateSizeAndPosition(z.size, z.xCoord , z.yCoord + z.size);
    bArr[3] = z;
    th.updateSizeAndPosition(th.size, th.xCoord , th.yCoord - th.size);
    bArr[0] = th;
    t.updateSizeAndPosition(t.size, t.xCoord, t.yCoord - t.size);
    bArr[1] = t;
    o.updateSizeAndPosition(o.size, o.xCoord, o.yCoord + o.size);
    bArr[2] = o;
    children = bArr;
 }
}
private void yreflect() {
  //at this point you figured out yreflect so just same implementation with different inputs

 // y-axis
 if (children.length == 4) {
  Block[] bArr = new Block[4];
  Block z = children[0];
  Block o = children[1];
  Block t = children[2];
  Block th = children[3];

  z.updateSizeAndPosition(z.size, z.xCoord - z.size, z.yCoord);
  bArr[1] = z;
  th.updateSizeAndPosition(th.size, th.xCoord - z.size , th.yCoord);
  bArr[2] = th;
  t.updateSizeAndPosition(t.size, t.xCoord + z.size, t.yCoord);
  bArr[3] = t;
  o.updateSizeAndPosition(o.size, o.xCoord + z.size, o.yCoord);
  bArr[0] = o;
  children = bArr;
 }

}


 /* Swaps the child Blocks of this Block.
  * If input is 1, flip over the y axis. If 0, flip over the x axis.
  * If this Block has no children, do nothing. The swap
  * should be propagate, effectively implementing a reflection
  * over the x-axis or over the y-axis. */
 public void reflect(int direction) {

  if (direction == 0) {
   xreflect();
  }
  else if(direction == 1) {
   yreflect();
  }
  else{
   throw new IllegalArgumentException("Please enter a valid direction please");
  }
 }


 private void counterRotate() {
  if (children.length == 4) {
   Block[] bArr = new Block[4];
   Block z = children[0];
   Block o = children[1];
   Block t = children[2];
   Block th = children[3];

   z.updateSizeAndPosition(z.size, z.xCoord - z.size, z.yCoord);
   bArr[1] = z;
   o.updateSizeAndPosition(o.size, o.xCoord, o.yCoord + z.size);
   bArr[2] = o;
   t.updateSizeAndPosition(t.size, t.xCoord+ z.size, t.yCoord);
   bArr[3] = t;
   th.updateSizeAndPosition(th.size, th.xCoord , th.yCoord - z.size);
   bArr[0] = th;
   children = bArr;
  }
 }

 private void clockRotate() {
  if (children.length == 4) {
   Block[] bArr = new Block[4];
   Block z = children[0];
   Block o = children[1];
   Block t = children[2];
   Block th = children[3];

   z.updateSizeAndPosition(z.size, z.xCoord, z.yCoord + z.size);
   bArr[3] = z;
   o.updateSizeAndPosition(o.size, o.xCoord + z.size, o.yCoord);
   bArr[0] = o;
   t.updateSizeAndPosition(t.size, t.xCoord, t.yCoord - z.size);
   bArr[1] = t;
   th.updateSizeAndPosition(th.size, th.xCoord - z.size, th.yCoord);
   bArr[2] = th;
   children = bArr;
  }
 }

 /* Rotate this Block and all its descendants.
  * If the input is 1, rotate clockwise. If 0, rotate
  * counterclockwise. If this Block has no children, do nothing.*/
 public void rotate(int direction) {
  //code here
  if (direction == 0) {
   counterRotate();
  }
  else if(direction == 1) {
   clockRotate();
  }
  else{
   throw new IllegalArgumentException("Please enter a valid direction for rotation please");
  }

 }



 /*
  * Smash this Block.
  *
  * If this Block can be smashed,
  * randomly generate four new children Blocks for it.
  * (If it already had children Blocks, discard them.)
  * Ensure that the invariants of the Blocks remain satisfied.
  *
  * A Block can be smashed if it is not the top-level Block
  * and it is not already at the level of the maximum depth.
  *
  * Return True if this Block was smashed and False otherwise.
  *
  */
 public boolean smash() {
  if (level < maxDepth && level != 0) {
   color = null;
   Block[] newChildren = new Block[4];
   //filling the new array with random blocks
   for (int i =0; i< 4;i++) {
    newChildren[i] = new Block(level + 1, maxDepth);
   }
   //Great, now you have an array with new blocks in it.
   // Where are they??? you need to put them in the right place
   int cs = size/2;
   newChildren[0].updateSizeAndPosition(cs, (xCoord + cs), yCoord);
   newChildren[1].updateSizeAndPosition(cs, xCoord, yCoord);
   newChildren[2].updateSizeAndPosition(cs, xCoord, yCoord + cs);
   newChildren[3].updateSizeAndPosition(cs, xCoord + cs, yCoord + cs);
   children = newChildren;
   return true;
  }
  return false;
 }


 /*
  * Return a two-dimensional array representing this Block as rows and columns of unit cells.
  *
  * Return and array arr where, arr[i] represents the unit cells in row i,
  * arr[i][j] is the color of unit cell in row i and column j.
  *
  * arr[0][0] is the color of the unit cell in the upper left corner of this Block.
  */
 public Color[][] flatten() {

  int unitSize = this.size/( (int)(Math.pow(2, maxDepth) ));
  Color[][] colourArr = new Color[unitSize][unitSize];

  for (int i = 0;i < size ;i++) {
   for (int j = 0;j < size ; j++) {
    colourArr[i][j] = (getSelectedBlock(j*unitSize, i*unitSize, maxDepth)).color;
   }
  }

  return colourArr;
 }



 // These two get methods have been provided. Do NOT modify them.
 public int getMaxDepth() {
  return this.maxDepth;
 }

 public int getLevel() {
  return this.level;
 }


 /*
  * The next 5 methods are needed to get a text representation of a block.
  * You can use them for debugging. You can modify these methods if you wish.
  */
 public String toString() {
  return String.format("pos=(%d,%d), size=%d, level=%d"
          , this.xCoord, this.yCoord, this.size, this.level);
 }

 public void printBlock() {
  this.printBlockIndented(0);
 }

 private void printBlockIndented(int indentation) {
  String indent = "";
  for (int i=0; i<indentation; i++) {
   indent += "\t";
  }

  if (this.children.length == 0) {
   // it's a leaf. Print the color!
   String colorInfo = GameColors.colorToString(this.color) + ", ";
   System.out.println(indent + colorInfo + this);
  } else {
   System.out.println(indent + this);
   for (Block b : this.children)
    b.printBlockIndented(indentation + 1);
  }
 }

 private static void coloredPrint(String message, Color color) {
  System.out.print(GameColors.colorToANSIColor(color));
  System.out.print(message);
  System.out.print(GameColors.colorToANSIColor(Color.WHITE));
 }

 public void printColoredBlock(){
  Color[][] colorArray = this.flatten();
  for (Color[] colors : colorArray) {
   for (Color value : colors) {
    String colorName = GameColors.colorToString(value).toUpperCase();
    if(colorName.length() == 0){
     colorName = "\u2588";
    }else{
     colorName = colorName.substring(0, 1);
    }
    coloredPrint(colorName, value);
   }
   System.out.println();
  }
 }

 public static void main(String[] args) {
  Block blockDepth2 = new Block(0,2);
  blockDepth2.updateSizeAndPosition(16, 0, 0);
  Color somec = (blockDepth2.getSelectedBlock(0, 0 , blockDepth2.maxDepth)).color;
  //blockDepth2.printColoredBlock();

 }

}