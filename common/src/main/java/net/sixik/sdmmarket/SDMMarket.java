package net.sixik.sdmmarket;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.currencies.CustomCurrencies;
import net.sixik.sdmmarket.common.MarketEvents;
import net.sixik.sdmmarket.common.commands.MarketCommands;
import net.sixik.sdmmarket.common.economy.SDMCoin;
import net.sixik.sdmmarket.common.network.MarketNetwork;
import net.sixik.sdmmarket.common.register.ItemsRegister;
import org.slf4j.Logger;

public class SDMMarket
{
	public static final String MOD_ID = "sdm_market";
	public static Logger LOGGER = LogUtils.getLogger();

	public static void init() {
        CustomCurrencies.CURRENCIES.put(SDMCoin.getId(), SDMCoin::new);

		MarketNetwork.init();
		ItemsRegister.ITEMS.register();
		MarketEvents.init();

		CommandRegistrationEvent.EVENT.register(MarketCommands::registerCommands);

		EnvExecutor.runInEnv(Env.CLIENT, () -> SDMMarketClient::init);

	}

    public static String moneyString(double money) {
        return String.format("◎ %,.2f", money);
    }

	public static String moneyString(String money) {
		return "◎ " + money;
	}

	public static void printStackTrace(String str, Throwable s){
		StringBuilder strBuilder = new StringBuilder(str);
		for (StackTraceElement stackTraceElement : s.getStackTrace()) {
			strBuilder.append("\t").append(" ").append("at").append(" ").append(stackTraceElement).append("\n");
		}
		str = strBuilder.toString();

		for (Throwable throwable : s.getSuppressed()) {
			printStackTrace(str, throwable);
		}

		Throwable ourCause = s.getCause();
		if(ourCause != null){
			printStackTrace(str, ourCause);
		}


		LOGGER.error(str);

	}

    public static double getBaseMoneyClient() {
        double countMoney = 0;
        final var pCur = EconomyAPI.getPlayerCurrencyClientData().getCurrency(SDMCoin.getId());
        if(pCur.isPresent()) {
            countMoney = pCur.get().balance;
        }
        return countMoney;
    }
}
