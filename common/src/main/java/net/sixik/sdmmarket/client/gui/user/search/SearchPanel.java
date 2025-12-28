package net.sixik.sdmmarket.client.gui.user.search;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmmarket.SDMMarket;
import net.sixik.sdmmarket.client.SearchData;
import net.sixik.sdmmarket.client.gui.user.MarketUserScreen;
import net.sixik.sdmmarket.client.widgets.MarketCheckBox;
import net.sixik.sdmmarket.client.widgets.MarketTextBox;
import net.sixik.sdmmarket.client.widgets.MarketTextField;
import net.sixik.v2.color.Colors;
import net.sixik.v2.color.RGB;
import net.sixik.v2.color.RGBA;
import net.sixik.v2.render.TextRenderHelper;

public class SearchPanel extends Panel {

    public TextBox searchBox;
    public TextBox minPriceBox;
    public TextBox maxPriceBox;
    public TextField categoryTitle;
    public SearchCategoriesPanel categoriesPanel;
    public PanelScrollBar scrollCategoriesPanel;
    public Button resetButton;
    public MarketTextField moneyField;
    public MarketCheckBox isEcnabledCheckBox;
    public MarketCheckBox isNoDamageCheckBox;

    public TextBox minCountBox;
    public TextBox maxCountBox;

    public SearchPanel(Panel panel) {
        super(panel);
    }

    @Override
    public void addWidgets() {
        add(searchBox = new MarketTextBox(this){
            @Override
            public void onTextChanged() {
                SearchData.name = searchBox.getText();

                ((MarketUserScreen)getGui()).refreshEntries();
            }
        });
        add(minPriceBox = new MarketTextBox(this) {
            @Override
            public void addMouseOverText(TooltipList list) {
                list.add(Component.translatable("sdm.market.user.minprice.tooltip"));
            }

            @Override
            public void onTextChanged() {
                if(!minPriceBox.getText().isEmpty()) {
                    SearchData.priceFrom = Integer.parseInt(minPriceBox.getText());
                    ((MarketUserScreen)getGui()).refreshEntries();
                }
            }

            @Override
            public boolean isValid(String txt) {
                if(txt.matches("\\d+")) {
                    long d = Integer.parseInt(txt);

                    if(SearchData.priceTo > 0 && d >= SearchData.priceTo) {
                        d = SearchData.priceTo - 1;
                        txt = String.valueOf(d);
                        setText(txt);
                        refreshWidgets();
                    }

                    if(SearchData.priceTo > 0) {
                        return d < SearchData.priceTo;
                    }
                    return true;
                }

                return false;
            }
        });
        add(maxPriceBox = new MarketTextBox(this){
            @Override
            public void addMouseOverText(TooltipList list) {
                list.add(Component.translatable("sdm.market.user.maxprice.tooltip"));
            }

            @Override
            public void onTextChanged() {
                if(!maxPriceBox.getText().isEmpty()) {
                    SearchData.priceTo = Integer.parseInt(maxPriceBox.getText());

                    ((MarketUserScreen)getGui()).refreshEntries();
                }


            }

            @Override
            public boolean isValid(String txt) {
                if(txt.matches("\\d+")) {

                    long d = Integer.parseInt(txt);

                    if(SearchData.priceFrom > 0 && d <= SearchData.priceFrom) {
                        d = SearchData.priceFrom + 1;
                        txt = String.valueOf(d);
                        setText(txt);
                        refreshWidgets();
                    }

                    if(SearchData.priceFrom > 0) {
                        return d > SearchData.priceFrom;
                    }
                    return true;
                }

                return false;
            }
        });

        add(minCountBox = new MarketTextBox(this){
            @Override
            public void addMouseOverText(TooltipList list) {
                list.add(Component.translatable("sdm.market.user.mincount.tooltip"));
            }

            @Override
            public void onTextChanged() {
                if(!minCountBox.getText().isEmpty()) {
                    SearchData.countFrom = Integer.parseInt(minCountBox.getText());
                    ((MarketUserScreen)getGui()).refreshEntries();
                }
            }

            @Override
            public boolean isValid(String txt) {
                if(txt.matches("\\d+")) {
                    long d = Integer.parseInt(txt);

                    if(SearchData.countTo > 0 && d >= SearchData.countTo) {
                        d = SearchData.countTo - 1;
                        txt = String.valueOf(d);
                        setText(txt);
                        refreshWidgets();
                    }

                    if(SearchData.countTo > 0) {
                        return d < SearchData.countTo;
                    }
                    return true;
                }

                return false;
            }
        });
        add(maxCountBox = new MarketTextBox(this){
            @Override
            public void addMouseOverText(TooltipList list) {
                list.add(Component.translatable("sdm.market.user.maxcount.tooltip"));
            }

            @Override
            public void onTextChanged() {
                if(!maxCountBox.getText().isEmpty()) {
                    SearchData.countTo = Integer.parseInt(maxCountBox.getText());

                    ((MarketUserScreen)getGui()).refreshEntries();
                }


            }

            @Override
            public boolean isValid(String txt) {
                if(txt.matches("\\d+")) {

                    long d = Integer.parseInt(txt);

                    if(SearchData.countFrom > 0 && d <= SearchData.countFrom) {
                        d = SearchData.countFrom + 1;
                        txt = String.valueOf(d);
                        setText(txt);
                        refreshWidgets();
                    }

                    if(SearchData.countFrom > 0) {
                        return d > SearchData.countFrom;
                    }
                    return true;
                }

                return false;
            }
        });

        add(categoryTitle = new TextField(this));
        categoryTitle.setText(Component.translatable("sdm.market.user.categories.title"));
        add(categoriesPanel = new SearchCategoriesPanel(this));
        categoriesPanel.addWidgets();

        add(scrollCategoriesPanel = new PanelScrollBar(this, categoriesPanel){
            @Override
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                Colors.UI_GOLD_0.draw(graphics,x,y,w,h);
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                RGBA.create(0,0,0, 255/2).draw(graphics,x,y,w,h);
            }
        });

        add(resetButton = new SimpleTextButton(this, Component.literal("Reset"), Icon.empty()) {
            @Override
            public void onClicked(MouseButton button) {
                if(button.isLeft()){
                    SearchData.reset();
                    parent.refreshWidgets();
                    ((MarketUserScreen)getGui()).refreshEntries();
                }
            }

            @Override
            public boolean renderTitleInCenter() {
                return true;
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                RGBA.create(100,100,100, 255/3).drawRoundFill(graphics,x,y,w,h, 4);
            }
        });

        add(moneyField = new MarketTextField(this));
        moneyField.setText(SDMMarket.moneyString(SDMMarket.getBaseMoneyClient()));

        add(isEcnabledCheckBox = new MarketCheckBox(this){
            @Override
            public void onClicked(MouseButton button) {
                super.onClicked(button);
                SearchData.isEncantable = isChecked;
                ((MarketUserScreen)getGui()).refreshEntries();
            }
        });
        isEcnabledCheckBox.setSelected(SearchData.isEncantable);
        this.isEcnabledCheckBox.setTitle(Component.translatable("sdm.market.user.search.isenchantable"));

        add(isNoDamageCheckBox = new MarketCheckBox(this){
            @Override
            public void onClicked(MouseButton button) {
                super.onClicked(button);
                SearchData.isNoDamaged = isChecked;
                ((MarketUserScreen)getGui()).refreshEntries();
            }
        });
        isNoDamageCheckBox.setSelected(SearchData.isNoDamaged);
        this.isNoDamageCheckBox.setTitle(Component.translatable("sdm.market.user.search.isnodamaged"));

        searchBox.setText(SearchData.name);
        searchBox.ghostText = Component.translatable("sdm.market.user.search.ghost_text").getString();
        minPriceBox.setText(SearchData.priceFrom > 0 ? String.valueOf(SearchData.priceFrom) : "");
        minPriceBox.ghostText = "◎ 0";
        maxPriceBox.setText(SearchData.priceTo > 0 ? String.valueOf(SearchData.priceTo) : "");
        maxPriceBox.ghostText = "◎ 1000";

        minCountBox.setText(SearchData.countFrom > 0 ? String.valueOf(SearchData.countFrom) : "");
        minCountBox.ghostText = "0";
        maxCountBox.setText(SearchData.countTo > 0 ? String.valueOf(SearchData.countTo) : "");
        maxCountBox.ghostText = "1000";

        setProperty();
    }

    @Override
    public void alignWidgets() {
        setProperty();
    }

    public void setProperty() {
        searchBox.setPos(4, 4);
        searchBox.setSize(this.width - 8, TextRenderHelper.getTextHeight() + 2);

        int wight = (this.width - this.width / 8 - 4) / 2;
        minPriceBox.setPos(4, searchBox.posY + searchBox.height + 2);
        minPriceBox.setSize(wight, TextRenderHelper.getTextHeight() + 2);
        maxPriceBox.setPos(this.width - wight - 4, searchBox.posY + searchBox.height + 2);
        maxPriceBox.setSize(wight, TextRenderHelper.getTextHeight() + 2);

        minCountBox.setPos(4, minPriceBox.posY + minPriceBox.height + 2);
        minCountBox.setSize(wight, TextRenderHelper.getTextHeight() + 2);
        maxCountBox.setPos(this.width - wight - 4, minPriceBox.posY + minPriceBox.height + 2);
        maxCountBox.setSize(wight, TextRenderHelper.getTextHeight() + 2);

        categoryTitle.setPos(4, minCountBox.posY + minCountBox.height + 4);
        categoryTitle.setSize(this.width - 8, TextRenderHelper.getTextHeight());

        this.categoriesPanel.setPos(4, categoryTitle.posY + categoryTitle.height + 2);

        int elementHeight = TextRenderHelper.getTextHeight() + 1 + 2;
        this.categoriesPanel.setSize(this.width - 8, elementHeight * 6 + 2);

        this.resetButton.setPos(4, this.height - ((TextRenderHelper.getTextHeight() + 2) * 2) - 2 - 1);
        this.resetButton.setSize(this.width - 8, TextRenderHelper.getTextHeight() + 1);

        this.moneyField.setWidth(this.width - 8);
        this.moneyField.setHeight(TextRenderHelper.getTextHeight());
        this.moneyField.setPos(4, this.height - (TextRenderHelper.getTextHeight() + 3));

        categoriesPanel.alignWidgets();

        this.isEcnabledCheckBox.setPos(4, categoriesPanel.posY + categoriesPanel.height + 2);
        this.isEcnabledCheckBox.setSize(this.width - 8, TextRenderHelper.getTextHeight() + 1);
        this.isNoDamageCheckBox.setPos(4, isEcnabledCheckBox.posY + isEcnabledCheckBox.height + 2);
        this.isNoDamageCheckBox.setSize(this.width - 8, TextRenderHelper.getTextHeight() + 1);

        this.scrollCategoriesPanel.setPosAndSize(
                this.categoriesPanel.getPosX() + this.categoriesPanel.getWidth() - this.getScrollbarWidth(),
                this.categoriesPanel.getPosY(),
                this.getScrollbarWidth(),
                this.categoriesPanel.getHeight()
        );
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RGBA.create(0,0,0,255/3).drawRoundFill(graphics,x,y,w,h,6);

        int a = minPriceBox.posX + minPriceBox.width;
        int b = maxPriceBox.posX + maxPriceBox.width;


        RGB.create(255,255,255).draw(graphics,
                x + minPriceBox.posX + minPriceBox.width + 2,
                y + minPriceBox.posY + minPriceBox.height / 2,
                b - a - 4 - minPriceBox.width,
                1
                );
        RGB.create(255,255,255).draw(graphics,
                x + minCountBox.posX + minCountBox.width + 2,
                y + minCountBox.posY + minCountBox.height / 2,
                b - a - 4 - minCountBox.width,
                1
                );
    }

    protected int getScrollbarWidth() {
        return 2;
    }
}
