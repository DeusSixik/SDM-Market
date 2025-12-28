package net.sixik.sdmmarket.common.market.user;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmmarket.SDMMarket;
import net.sixik.sdmmarket.api.MarketAPI;
import net.sixik.sdmmarket.client.SearchData;
import net.sixik.sdmmarket.common.data.MarketDataManager;
import net.sixik.sdmmarket.common.data.MarketPlayerData;
import net.sixik.sdmmarket.common.economy.SDMCoin;
import net.sixik.sdmmarket.common.market.basketEntry.BasketItemEntry;
import net.sixik.sdmmarket.common.market.basketEntry.BasketMoneyEntry;
import net.sixik.sdmmarket.common.market.offer.OfferCreateData;
import net.sixik.sdmmarket.common.network.user.SyncMarketDataS2C;
import net.sixik.sdmmarket.common.network.user.SyncUserDataS2C;
import net.sixik.sdmmarket.common.network.user.UpdateUIS2C;
import net.sixik.sdmmarket.common.utils.INBTSerialize;
import net.sixik.sdmmarket.common.utils.NBTUtils;

import java.util.Objects;
import java.util.UUID;

public class MarketUserEntry implements INBTSerialize {

    public UUID categoryID;
    public UUID entryID;
    public UUID ownerID;
    public boolean isSold = false;
    public ItemStack itemStack;
    public int count = 1;
    public long price = 0;

    public MarketUserEntry(){
        this(UUID.randomUUID());
    }

    public MarketUserEntry(UUID categoryID) {
        this(ItemStack.EMPTY, categoryID);
    }

    public MarketUserEntry(ItemStack itemStack, UUID categoryID) {
        this.itemStack = itemStack;
        this.entryID = UUID.randomUUID();
        this.categoryID = categoryID;
    }

    public boolean isVisiable() {

        if(SearchData.isEncantable && !itemStack.isEnchanted()) return false;
        if(SearchData.isNoDamaged && itemStack.isDamaged()) return false;

        if(SearchData.priceFrom <= 0 || price >= SearchData.priceFrom) {
            if(SearchData.priceTo <= 0 || price <= SearchData.priceTo) {

                if(SearchData.countFrom <= 0 || count >= SearchData.countFrom) {
                    if(SearchData.countTo <= 0 || count <= SearchData.countTo) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static MarketUserEntry fromOfferData(UUID categoryID, OfferCreateData createData){
        MarketUserEntry entry = new MarketUserEntry(categoryID);
        entry.count = createData.count;
        entry.price = createData.price;
        entry.ownerID = createData.ownerID;
        entry.itemStack = createData.item;
        return entry;
    }

    public void closeEntry(ServerPlayer player) {
        try {
            MarketPlayerData.PlayerData data = MarketDataManager.getPlayerData(player);
            if (data == null) {
                SDMMarket.LOGGER.error("Could not find MarketPlayerData for player " + player);
                return;
            }

            boolean removed = removeEntry();

            if (removed && data.playerOffers.removeIf(s -> Objects.equals(s, entryID))) {
                data.countOffers = Math.min(data.countOffers + 1, MarketDataManager.GLOBAL_CONFIG_SERVER.maxOffersForPlayer);

                BasketItemEntry itemEntry = new BasketItemEntry(itemStack, count);
                data.playerBasket.basketMoneyEntries.add(itemEntry);


                MarketAPI.syncMarket(player.server);

                new SyncUserDataS2C(data.serialize()).sendTo(player);

                MarketDataManager.savePlayer(player.server, player);
                MarketDataManager.saveMarketData(player.server);
            }
        } catch (Exception e){
            SDMMarket.printStackTrace("", e);
        }
    }

    private boolean removeEntry() {
        for (MarketUserCategory category : MarketDataManager.USER_SERVER.categories) {
            for (MarketUserEntryList entry : category.entries) {
                var iterator = entry.entries.iterator();
                while (iterator.hasNext()) {
                    var userEntry = iterator.next();
                    if(Objects.equals(userEntry.entryID, entryID)) {
                        iterator.remove();
                        SDMMarket.LOGGER.info("Removed entry " + userEntry.entryID);
                        return true;
                    }
                }
            }
        }

        SDMMarket.LOGGER.error("Couldn't remove entry: " + entryID);
        return false;
    }

    public boolean buyEntry(ServerPlayer player) {
        MarketPlayerData.PlayerData data = MarketDataManager.getPlayerData(player);
        MarketPlayerData.PlayerData ownerData = MarketDataManager.getPlayerData(player.server, ownerID);

        final var curData = EconomyAPI.getPlayerCurrencyServerData();

        if(curData.getBalance(player, SDMCoin.getId()).value < price) return false;

        if(ownerData == null || data == null) return false;

        BasketMoneyEntry moneyEntry = new BasketMoneyEntry(price);
        ownerData.playerBasket.basketMoneyEntries.add(moneyEntry);
        ownerData.countOffers = Math.min(ownerData.countOffers + 1, MarketDataManager.GLOBAL_CONFIG_SERVER.maxOffersForPlayer);
        ownerData.playerOffers.removeIf(s -> Objects.equals(s, entryID));


        curData.addCurrencyValue(player, SDMCoin.getId(), -price);
        BasketItemEntry itemEntry = new BasketItemEntry(itemStack, count);
        data.playerBasket.basketMoneyEntries.add(itemEntry);

        MarketDataManager.savePlayer(player.server, data.playerID);
        MarketDataManager.savePlayer(player.server, ownerData.playerID);

        new UpdateUIS2C().sendTo(player);

        new SyncUserDataS2C(data.serialize()).sendTo(player);

        ServerPlayer ownerPlayer = player.server.getPlayerList().getPlayer(ownerID);
        if(ownerPlayer != null) new SyncUserDataS2C(ownerData.serialize()).sendTo(ownerPlayer);


        return true;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID("categoryID", categoryID);
        nbt.putUUID("entryID", entryID);
        nbt.putUUID("ownerID", ownerID);
        nbt.putBoolean("isSold", isSold);
        NBTUtils.putItemStack(nbt,"item", itemStack);
        nbt.putInt("count", count);
        nbt.putLong("price", price);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        this.categoryID = nbt.getUUID("categoryID");
        this.entryID = nbt.getUUID("entryID");
        this.ownerID = nbt.getUUID("ownerID");
        this.isSold = nbt.getBoolean("isSold");
        this.itemStack = NBTUtils.getItemStack(nbt, "item");
        this.count = nbt.getInt("count");
        this.price = nbt.getLong("price");
    }
}
