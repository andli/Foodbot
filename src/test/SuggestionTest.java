package andli.foodbot.test;

import junit.framework.TestCase;
import andli.foodbot.FoodTime;
import andli.foodbot.FoodUser;
import andli.foodbot.modules.Suggestion;

public class SuggestionTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testEqualsSuggestion() {
		try {
			FoodUser fu = new FoodUser();
			fu.setNick("Test");
			Suggestion s1 = new Suggestion(fu, new FoodTime("11:00"), "Restaurant");
			Suggestion s2 = new Suggestion(fu, new FoodTime("11:00"), "Restaurant");
			assertEquals(s1, s2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
