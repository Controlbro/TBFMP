package com.tbfmc.tbfmp.economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class VaultEconomyProvider implements Economy {
    private final BalanceStorage storage;

    public VaultEconomyProvider(BalanceStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "TBFMP";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        return String.format("%.2f", amount);
    }

    @Override
    public String currencyNamePlural() {
        return "credits";
    }

    @Override
    public String currencyNameSingular() {
        return "credit";
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return true;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return true;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return storage.getBalance(player.getUniqueId());
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Invalid amount");
        }
        if (!has(player, amount)) {
            return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
        }
        storage.subtractBalance(player.getUniqueId(), amount);
        return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Invalid amount");
        }
        storage.addBalance(player.getUniqueId(), amount);
        return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return bankUnsupported();
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return bankUnsupported();
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return bankUnsupported();
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return bankUnsupported();
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return bankUnsupported();
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return bankUnsupported();
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return bankUnsupported();
    }

    @Override
    public EconomyResponse isBankOwner(String name, String player) {
        return bankUnsupported();
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return bankUnsupported();
    }

    @Override
    public EconomyResponse isBankMember(String name, String player) {
        return bankUnsupported();
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return bankUnsupported();
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        storage.setBalance(player.getUniqueId(), storage.getBalance(player.getUniqueId()));
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }

    private EconomyResponse bankUnsupported() {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bank support not available");
    }
}
