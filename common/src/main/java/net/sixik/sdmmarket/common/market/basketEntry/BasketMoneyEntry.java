package net.sixik.sdmmarket.common.market.basketEntry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.utils.ErrorCodes;
import net.sixik.sdmmarket.common.economy.SDMCoin;

public class BasketMoneyEntry extends AbstractBasketEntry{

    public long moneyCount;
    public BasketMoneyEntry(long moneyCount){
        super();
        this.moneyCount = moneyCount;
    }


    @Override
    public void givePlayer(Player player) {
        if(player.isLocalPlayer()) {
            final var data = EconomyAPI.getPlayerCurrencyClientData().getCurrency(SDMCoin.getId());
            if(data.isEmpty()) return;
            final var _data = data.get();
            _data.balance = _data.balance + moneyCount;
        } else {
            EconomyAPI.getPlayerCurrencyServerData().addCurrencyValue(player, SDMCoin.getId(), moneyCount);
        }
    }

    @Override
    public String getID() {
        return "basketMoneyEntry";
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putLong("moneyCount", moneyCount);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        this.moneyCount = nbt.getLong("moneyCount");
    }
}
