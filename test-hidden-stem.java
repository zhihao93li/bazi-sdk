import com.tafu.bazi.sdk.BaziCalculator;
import com.tafu.bazi.sdk.BaziCalculatorImpl;
import com.tafu.bazi.sdk.model.*;

public class TestHiddenStem {
    public static void main(String[] args) {
        BaziCalculator calculator = new BaziCalculatorImpl();
        
        // 测试案例：找一个日主天干与藏干天干相同的例子
        BaziRequest request = BaziRequest.builder()
            .year(1990)
            .month(6)
            .day(15)
            .hour(14)
            .minute(30)
            .calendarType("solar")
            .gender("male")
            .build();

        BaziResponse response = calculator.calculate(request);
        
        System.out.println("=== 八字排盘结果 ===");
        System.out.println("日主天干: " + response.getDayMaster().getGan());
        System.out.println();
        
        // 检查四柱藏干
        FourPillarsDTO fourPillars = response.getFourPillars();
        
        System.out.println("年柱: " + fourPillars.getYear().getHeavenlyStem().getChinese() + 
                          fourPillars.getYear().getEarthlyBranch().getChinese());
        printHiddenStems(fourPillars.getYear());
        
        System.out.println("\n月柱: " + fourPillars.getMonth().getHeavenlyStem().getChinese() + 
                          fourPillars.getMonth().getEarthlyBranch().getChinese());
        printHiddenStems(fourPillars.getMonth());
        
        System.out.println("\n日柱: " + fourPillars.getDay().getHeavenlyStem().getChinese() + 
                          fourPillars.getDay().getEarthlyBranch().getChinese());
        printHiddenStems(fourPillars.getDay());
        
        System.out.println("\n时柱: " + fourPillars.getHour().getHeavenlyStem().getChinese() + 
                          fourPillars.getHour().getEarthlyBranch().getChinese());
        printHiddenStems(fourPillars.getHour());
    }
    
    private static void printHiddenStems(PillarDTO pillar) {
        System.out.println("藏干信息:");
        for (HiddenStemDTO stem : pillar.getHiddenStems()) {
            System.out.println("  - " + stem.getChinese() + 
                              " | 五行: " + stem.getElement() + 
                              " | 阴阳: " + stem.getYinYang() + 
                              " | 十神: " + stem.getTenGod());
        }
    }
}
