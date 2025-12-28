package net.sixik.sdmmarket.client.gui.user.buyer;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.utils.CurrencyHelper;
import net.sixik.sdmmarket.SDMMarket;
import net.sixik.sdmmarket.client.widgets.MarketTextField;
import net.sixik.sdmmarket.common.economy.SDMCoin;
import net.sixik.v2.color.RGBA;
import net.sixik.v2.render.TextRenderHelper;

public class MarketUserBuyerInfoPanel extends Panel {

    public Icon icon;
    public MarketUserBuyerScreen panel;
    public MarketUserBuyerBuyButton buyerBuyButton;

    public TextField moneyField;
    public MarketUserBuyerInfoPanel(MarketUserBuyerScreen panel) {
        super(panel);
        this.panel = panel;
        this.icon = ItemIcon.getItemIcon(panel.entryList.itemStack);
    }

    @Override
    public void addWidgets() {
        add(moneyField = new MarketTextField(this));
        add(buyerBuyButton = new MarketUserBuyerBuyButton(this));
    }

    @Override
    public void alignWidgets() {
        addElements();
    }

    public void addElements() {
        moneyField.setSize(this.width - 4, (TextRenderHelper.getTextHeight() + 1) * 2);
        moneyField.setPos(2,  36);
        moneyField.setText(Component.translatable("sdm.market.user.buy.player_money", SDMMarket.getBaseMoneyClient()));
        moneyField.setMaxWidth(this.width - 4);

        this.buyerBuyButton.setPos(4, this.height - (TextRenderHelper.getTextHeight() + 3));
        this.buyerBuyButton.setSize(this.width - 8, TextRenderHelper.getTextHeight() + 1);
    }

    public boolean canBuy() {
        return panel.selectedEntry!= null && SDMMarket.getBaseMoneyClient() >= panel.selectedEntry.price;
    }

    public boolean isSelected() {
        return panel.selectedEntry != null;
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RGBA.create(0,0,0,255/3).drawRoundFill(graphics,x,y,w,h,6);

        int j = w >= 34 ? 32 : 16;



        RGBA.create(100,100,100,255/3).drawRoundFill(graphics,x + (this.width / 2) - j / 2,y + 2,j,j,6);
        icon.draw(graphics,x + (this.width / 2) - j / 2,y + 2, j, j);



    }
}
