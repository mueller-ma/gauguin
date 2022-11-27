package com.holokenmod.creation;

import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridSize;
import com.holokenmod.options.CurrentGameOptionsVariant;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GridCageOperation;
import com.holokenmod.options.SingleCageUsage;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

public class TestGridDifficulty {
	@Disabled
	@RepeatedTest(20)
	void testDifficulty() {
		CurrentGameOptionsVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
		CurrentGameOptionsVariant.getInstance().setShowOperators(true);
		CurrentGameOptionsVariant.getInstance().setSingleCageUsage(SingleCageUsage.FIXED_NUMBER);
		CurrentGameOptionsVariant.getInstance().setCageOperation(GridCageOperation.OPERATIONS_ALL);
		
		GridCreator creator = new GridCreator(new GridSize(9, 9));
		
		Grid grid = creator.createRandomizedGridWithCages();
		
		System.out.println(new GridDifficulty(grid).calculate());
	}
	
	@Disabled
	@Test
	void calculateValues() {
		CurrentGameOptionsVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
		CurrentGameOptionsVariant.getInstance().setShowOperators(true);
		CurrentGameOptionsVariant.getInstance().setSingleCageUsage(SingleCageUsage.FIXED_NUMBER);
		CurrentGameOptionsVariant.getInstance().setCageOperation(GridCageOperation.OPERATIONS_ALL);
		
		ArrayList<BigInteger> difficulties = new ArrayList<>();
		
		for (int i = 0; i < 10000; i++) {
			GridCreator creator = new GridCreator(new GridSize(9, 9));
			Grid grid = creator.createRandomizedGridWithCages();
			
			difficulties.add(new GridDifficulty(grid).calculate());
			System.out.print(".");
		}
		
		Collections.sort(difficulties);
		
		System.out.println(difficulties.size());
		System.out.println("333: " + difficulties.get(3332));
		System.out.println("667: " + difficulties.get(6666));
	}
}
