package com.holokenmod;

import android.util.Log;

import com.holokenmod.ui.DigitSetting;

import java.util.ArrayList;
import java.util.Arrays;

public class GridCage {
  

    public static final int CAGE_1 = 0;

  // Action for the cage
  public GridCageAction mAction;
  public String mActionStr;
  // Number the action results in
  public int mResult;
  // List of cage's cells
  private ArrayList<GridCell> mCells;
  // Type of the cage
  public int mType;
  // Id of the cage
  public int mId;
  
  private final Grid grid;
  
  // User math is correct
  public boolean mUserMathCorrect;
  // Cage (or a cell within) is selected
  public boolean mSelected;

  // Cached list of numbers which satisfy the cage's arithmetic
  private ArrayList<int[]> mPossibles;
  
  public GridCage (Grid grid, int type) {
      this.grid = grid;
      mType = type;
      mPossibles = null;
      mUserMathCorrect = true;
      mSelected = false;
      mCells = new ArrayList<>();
  }
  
  public String toString() {
      String retStr = "";
      retStr += "Cage id: " + this.mId + ", Type: " + this.mType;
      retStr += ", Action: ";
      switch (this.mAction)
      {
      case ACTION_NONE:
          retStr += "None"; break;
      case ACTION_ADD:
          retStr += "Add"; break;
      case ACTION_SUBTRACT:
          retStr += "Subtract"; break;
      case ACTION_MULTIPLY:
          retStr += "Multiply"; break;
      case ACTION_DIVIDE:
          retStr += "Divide"; break;
      }

      retStr += ", ActionStr: " + this.mActionStr + ", Result: " + this.mResult;
      retStr += ", cells: " + getCellNumbers();

      return retStr;
  }

  /*
   * Generates the arithmetic for the cage, semi-randomly.
   * 
   * - If a cage has 3 or more cells, it can only be an add or multiply.
   * - else if the cells are evenly divisible, division is used, else
   *   subtraction.
   */
  public void setArithmetic(GridCageOperation operationSet) {
    this.mAction = null;
    if (this.mType == CAGE_1) {
      this.mAction = GridCageAction.ACTION_NONE;
      this.mActionStr = "";
      this.mResult = this.mCells.get(0).getValue();
      return;
    }
    double rand = RandomSingleton.getInstance().nextDouble();
    double addChance = 0.25;
    double multChance = 0.5;
    
    if (operationSet == GridCageOperation.OPERATIONS_ADD_SUB) {
        if (this.mCells.size() > 2) 
            addChance = 1.0;
        else
            addChance = 0.4;
        multChance = 0.0;
    }
    else if (operationSet == GridCageOperation.OPERATIONS_MULT) {
        addChance = 0.0;
        multChance = 1.0;
    }
    else if (this.mCells.size() > 2 || operationSet == GridCageOperation.OPERATIONS_ADD_MULT) { // force + and x only
        addChance = 0.5;
        multChance = 1.0;
    }

    if (rand <= addChance)
      this.mAction = GridCageAction.ACTION_ADD;
    else if (rand <= multChance)
      this.mAction = GridCageAction.ACTION_MULTIPLY;
    
    if (this.mAction == GridCageAction.ACTION_ADD) {
      int total = 0;
      for (GridCell cell : this.mCells) {
        total += cell.getValue();
      }
      this.mResult = total;
      this.mActionStr = "+";
    }
    if (this.mAction == GridCageAction.ACTION_MULTIPLY) {
      int total = 1;
      for (GridCell cell : this.mCells) {
        total *= cell.getValue();
      }
      this.mResult = total;
      this.mActionStr = "x";
    }
    if (this.mAction != null) {
      return;
    }

    if (this.mCells.size() < 2) {
        Log.d("KenKen", "Why only length 1? Type: " + this);
    }
    int cell1Value = this.mCells.get(0).getValue();
    int cell2Value = this.mCells.get(1).getValue();
    int higher = cell1Value;
    int lower = cell2Value;
    boolean canDivide = false;
    if (cell1Value < cell2Value) {
      higher = cell2Value;
      lower = cell1Value;
    }
    if (lower > 0 && higher % lower == 0 && operationSet != GridCageOperation.OPERATIONS_ADD_SUB)
      canDivide = true;
    if (canDivide) {
      this.mResult = higher / lower;
      this.mAction = GridCageAction.ACTION_DIVIDE;
      // this.mCells.get(0).mCageText = this.mResult + "\367";
      this.mActionStr = "/";
    } else {
      this.mResult = higher - lower;
      this.mAction = GridCageAction.ACTION_SUBTRACT;
      this.mActionStr = "-";
    }
  }
  
  /*
   * Sets the cageId of the cage's cells.
   */
  public void setCageId(int id) {
    this.mId = id;
    for (GridCell cell : this.mCells)
      cell.setCage(this);
  }
  
  
  public boolean isAddMathsCorrect()
  {
      int total = 0;
      for (GridCell cell : this.mCells) {
          total += cell.getUserValue();
      }
      return (total == this.mResult);
  }

  public boolean isMultiplyMathsCorrect()
  {
      int total = 1;
      for (GridCell cell : this.mCells) {
          total *= cell.getUserValue();
      }
      return (total == this.mResult);
  }

  public boolean isDivideMathsCorrect()
  {
      if (this.mCells.size() != 2)
          return false;
      
      if (this.mCells.get(0).getUserValue() > this.mCells.get(1).getUserValue())
          return this.mCells.get(0).getUserValue() == (this.mCells.get(1).getUserValue() * this.mResult);
      else
          return this.mCells.get(1).getUserValue() == (this.mCells.get(0).getUserValue() * this.mResult);
  }

  public boolean isSubtractMathsCorrect()
  {
      if (this.mCells.size() != 2)
          return false;

      if (this.mCells.get(0).getUserValue() > this.mCells.get(1).getUserValue())
          return (this.mCells.get(0).getUserValue() - this.mCells.get(1).getUserValue()) == this.mResult;
      else
          return (this.mCells.get(1).getUserValue() - this.mCells.get(0).getUserValue()) == this.mResult;
  }
  
  // Returns whether the user values in the cage match the cage text
  public boolean isMathsCorrect() {
      if (this.mCells.size() == 1)
          return this.mCells.get(0).isUserValueCorrect();

      if (GameVariant.getInstance().showOperators()) {
          switch (this.mAction) {
                case ACTION_ADD :
                    return isAddMathsCorrect();
                case ACTION_MULTIPLY :
                    return isMultiplyMathsCorrect();
                case ACTION_DIVIDE :
                    return isDivideMathsCorrect();
                case ACTION_SUBTRACT :
                    return isSubtractMathsCorrect();
          }
      }
      else {
          return isAddMathsCorrect() || isMultiplyMathsCorrect() ||
                  isDivideMathsCorrect() || isSubtractMathsCorrect();

      }
      throw new RuntimeException("isSolved() got to an unreachable point " + 
              this.mAction + ": " + this.toString());
  }
  
  // Determine whether user entered values match the arithmetic.
  //
  // Only marks cells bad if all cells have a uservalue, and they dont
  // match the arithmetic hint.
  public void userValuesCorrect() {
    this.mUserMathCorrect = true;
    for (GridCell cell : this.mCells)
      if (!cell.isUserValueSet()) {
        this.setBorders();
        return;
      }
    this.mUserMathCorrect = this.isMathsCorrect();
    this.setBorders();
  }
  
  /*
   * Sets the borders of the cage's cells.
   */
  public void setBorders() {
    for (GridCell cell : this.mCells) {
        for(Direction direction : Direction.values()) {
            cell.getCellBorders().setBorderType(direction, GridBorderType.BORDER_NONE);
        }
      if (this.grid.getCage(cell.getRow()-1, cell.getColumn()) != this)
        if (!this.mUserMathCorrect && GameVariant.getInstance().showBadMaths())
            cell.getCellBorders().setBorderType(Direction.NORTH, GridBorderType.BORDER_WARN);
        else if (this.mSelected)
            cell.getCellBorders().setBorderType(Direction.NORTH, GridBorderType.BORDER_CAGE_SELECTED);
        else
            cell.getCellBorders().setBorderType(Direction.NORTH, GridBorderType.BORDER_SOLID);

      if (this.grid.getCage(cell.getRow(), cell.getColumn()+1) != this)
          if(!this.mUserMathCorrect && GameVariant.getInstance().showBadMaths())
              cell.getCellBorders().setBorderType(Direction.EAST, GridBorderType.BORDER_WARN);
          else if (this.mSelected)
              cell.getCellBorders().setBorderType(Direction.EAST, GridBorderType.BORDER_CAGE_SELECTED);
          else
              cell.getCellBorders().setBorderType(Direction.EAST, GridBorderType.BORDER_SOLID);

      if (this.grid.getCage(cell.getRow()+1, cell.getColumn()) != this)
        if(!this.mUserMathCorrect && GameVariant.getInstance().showBadMaths())
            cell.getCellBorders().setBorderType(Direction.SOUTH, GridBorderType.BORDER_WARN);
        else if (this.mSelected)
            cell.getCellBorders().setBorderType(Direction.SOUTH, GridBorderType.BORDER_CAGE_SELECTED);
        else
            cell.getCellBorders().setBorderType(Direction.SOUTH, GridBorderType.BORDER_SOLID);

      if (this.grid.getCage(cell.getRow(), cell.getColumn()-1) != this)
        if(!this.mUserMathCorrect && GameVariant.getInstance().showBadMaths())
            cell.getCellBorders().setBorderType(Direction.WEST, GridBorderType.BORDER_WARN);
        else if (this.mSelected)
            cell.getCellBorders().setBorderType(Direction.WEST, GridBorderType.BORDER_CAGE_SELECTED);
        else
            cell.getCellBorders().setBorderType(Direction.WEST, GridBorderType.BORDER_SOLID);
    }
  }

public ArrayList<int[]> getPossibleNums()
{
    if (mPossibles == null) {
        if (GameVariant.getInstance().showOperators())
            mPossibles = setPossibleNums();
        else
            mPossibles = setPossibleNumsNoOperator();
    }
    return mPossibles;
}

private ArrayList<int[]> setPossibleNumsNoOperator()
{
    ArrayList<int[]> AllResults = new ArrayList<int[]>();

    if (this.mAction == GridCageAction.ACTION_NONE) {
        assert (mCells.size() == 1);
        int number[] = {mResult};
        AllResults.add(number);
        return AllResults;
    }
    
    if (mCells.size() == 2) {
        for (int i1 = 1; i1<=this.grid.getGridSize(); i1++)
            for (int i2 = i1+1; i2<=this.grid.getGridSize(); i2++)
                if (i2 - i1 == mResult || i1 - i2 == mResult || mResult*i1 == i2 || 
                        mResult*i2 == i1 || i1+i2 == mResult || i1*i2 == mResult) {
                    int numbers[] = {i1, i2};
                    AllResults.add(numbers);
                    numbers = new int[] {i2, i1};
                    AllResults.add(numbers);
                }
        return AllResults;
    }

    // ACTION_ADD:
    AllResults = getalladdcombos(mResult,mCells.size());
    
    // ACTION_MULTIPLY:
    ArrayList<int[]> multResults = getallmultcombos(mResult,mCells.size());
    
    // Combine Add & Multiply result sets
    for (int[] possibleset: multResults)
    {
        boolean foundset = false;
        for (int[] currentset: AllResults) {
            if (Arrays.equals(possibleset, currentset)) {
                foundset = true;
                break;
            }
        }
        if (!foundset)
            AllResults.add(possibleset);
    }
        
    return AllResults;
}
  
/*
 * Generates all combinations of numbers which satisfy the cage's arithmetic
 * and MathDoku constraints i.e. a digit can only appear once in a column/row 
 */
private ArrayList<int[]> setPossibleNums()
{
    ArrayList<int[]> AllResults = new ArrayList<int[]>();

    switch (this.mAction) {
    case ACTION_NONE:
        assert (mCells.size() == 1);
        int number[] = {mResult};
        AllResults.add(number);
        break;
      case ACTION_SUBTRACT:
          assert(mCells.size() == 2);
          for (int i1 : grid.getPossibleDigits()) {
              for (int i2 = i1 + 1; i2 <= grid.getMaximumDigit(); i2++) {
                  if (i2 - i1 == mResult || i1 - i2 == mResult) {
                      int numbers[] = {i1, i2};
                      AllResults.add(numbers);
                      numbers = new int[]{i2, i1};
                      AllResults.add(numbers);
                  }
              }
          }
        break;
      case ACTION_DIVIDE:
          assert(mCells.size() == 2);
          for (int i1 : grid.getPossibleDigits()) {
              for (int i2 = i1 + 1; i2 <= grid.getMaximumDigit(); i2++) {
                  if (mResult * i1 == i2 || mResult * i2 == i1) {
                      int numbers[] = {i1, i2};
                      AllResults.add(numbers);
                      numbers = new int[]{i2, i1};
                      AllResults.add(numbers);
                  }
              }
          }
          break;
      case ACTION_ADD:
          AllResults = getalladdcombos(mResult,mCells.size());
          break;
      case ACTION_MULTIPLY:
          AllResults = getallmultcombos(mResult,mCells.size());
          break;
    }
    return AllResults;
}

// The following two variables are required by the recursive methods below.
// They could be passed as parameters of the recursive methods, but this
// reduces performance.
private int[] numbers;
private ArrayList<int[]> result_set;

private ArrayList<int[]> getalladdcombos (int target_sum, int n_cells)
{
    numbers = new int[n_cells];
    result_set = new ArrayList<int[]> ();
    getaddcombos(target_sum, n_cells);
    return result_set;
}

private void getaddcombos(int target_sum, int n_cells)
{
    for (int n : grid.getPossibleDigits())
    {
        if (n_cells == 1)
        {
            if (n == target_sum) {
                numbers[0] = n;
                if (satisfiesConstraints(numbers))
                    result_set.add(numbers.clone());
            }
        }
        else {
            numbers[n_cells-1] = n;
            getaddcombos(target_sum-n, n_cells-1);
        }
    }
    return;
}

private ArrayList<int[]> getallmultcombos (int target_sum, int n_cells)
{
    numbers = new int[n_cells];
    result_set = new ArrayList<int[]> ();
    getmultcombos(target_sum, n_cells);
    
    return result_set;
}

private void getmultcombos(int target_sum, int n_cells)
{
    for (int n : grid.getPossibleDigits())
    {
        if (n != 0 && target_sum % n != 0)
            continue;
        
        if (n_cells == 1)
        {
            if (n == target_sum) {
                numbers[0] = n;
                if (satisfiesConstraints(numbers))
                    result_set.add(numbers.clone());
            }
        }
        else {
            numbers[n_cells-1] = n;
            if (n == 0) {
                getmultcombos(target_sum, n_cells - 1);
            } else {
                getmultcombos(target_sum / n, n_cells - 1);
            }
        }
    }
    return;
}

/*
 * Check whether the set of numbers satisfies all constraints
 * Looking for cases where a digit appears more than once in a column/row
 * Constraints:
 * 0 -> (getGrid().getGridSize() * getGrid().getGridSize())-1 = column constraints
 * (each column must contain each digit) 
 * getGrid().getGridSize() * getGrid().getGridSize() -> 2*(getGrid().getGridSize() * getGrid().getGridSize())-1 = row constraints
 * (each row must contain each digit) 
 */
private boolean satisfiesConstraints(int[] test_nums) {
    boolean constraints[] = new boolean[grid.getGridSize()* grid.getGridSize()*2];
    int constraint_num;

    for (int i = 0; i<this.mCells.size(); i++) {
        int numberToTestIndex = test_nums[i];

        if (ApplicationPreferences.getInstance().getDigitSetting() == DigitSetting.FIRST_DIGIT_ONE) {
            numberToTestIndex = numberToTestIndex - 1;
        }

        constraint_num = grid.getGridSize() * numberToTestIndex + mCells.get(i).getColumn();
        if (constraints[constraint_num])
            return false;
        else
            constraints[constraint_num]= true;
        constraint_num = grid.getGridSize() * grid.getGridSize() + grid.getGridSize() * numberToTestIndex+ mCells.get(i).getRow();
        if (constraints[constraint_num])
            return false;
        else
            constraints[constraint_num]= true;
    }

    return true;
}

    public int getId() {
        return mId;
    }

    public void addCell(GridCell cell) {
        this.mCells.add(cell);
    }

    public String getCellNumbers() {
        String numbers = "";

        for (GridCell cell : this.mCells) {
            numbers += cell.getCellNumber() + ",";
        }

        return numbers;
    }

    public void setCagetext(String cageText) {
        this.mCells.get(0).setCagetext(cageText);
    }

    public int getNumberOfCells() {
        return this.mCells.size();
    }

    public GridCell getCell(int cellNumber) {
        return this.mCells.get(cellNumber);
    }

    public ArrayList<GridCell> getCells() {
        return this.mCells;
    }
}