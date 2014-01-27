-- g9 - BioLock
-- Top level system file
library ieee;

use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.VITAL_Primatives.all;

entity BioLock is
        port (
            reset_reset_n                           : in    std_logic                     := 'X';             -- reset_n
            gpio_external_connection_export         : out   std_logic;                                        -- export
            switches_external_connection_export     : in    std_logic_vector(15 downto 0) := (others => 'X'); -- export
            green_leds_external_connection_export   : out   std_logic_vector(7 downto 0);                     -- export
            character_lcd_0_external_interface_DATA : inout std_logic_vector(7 downto 0)  := (others => 'X'); -- DATA
            character_lcd_0_external_interface_ON   : out   std_logic;                                        -- ON
            character_lcd_0_external_interface_BLON : out   std_logic;                                        -- BLON
            character_lcd_0_external_interface_EN   : out   std_logic;                                        -- EN
            character_lcd_0_external_interface_RS   : out   std_logic;                                        -- RS
            character_lcd_0_external_interface_RW   : out   std_logic;                                        -- RW
            altpll_0_c0_clk                         : out   std_logic;                                        -- clk
            serial_external_connection_rxd          : in    std_logic                     := 'X';             -- rxd
            serial_external_connection_txd          : out   std_logic;                                        -- txd
            sdram_0_wire_addr                       : out   std_logic_vector(11 downto 0);                    -- addr
            sdram_0_wire_ba                         : out   std_logic_vector(1 downto 0);                     -- ba
            sdram_0_wire_cas_n                      : out   std_logic;                                        -- cas_n
            sdram_0_wire_cke                        : out   std_logic;                                        -- cke
            sdram_0_wire_cs_n                       : out   std_logic;                                        -- cs_n
            sdram_0_wire_dq                         : inout std_logic_vector(15 downto 0) := (others => 'X'); -- dq
            sdram_0_wire_dqm                        : out   std_logic_vector(1 downto 0);                     -- dqm
            sdram_0_wire_ras_n                      : out   std_logic;                                        -- ras_n
            sdram_0_wire_we_n                       : out   std_logic;                                        -- we_n
            clk_clk                                 : in    std_logic                     := 'X';             -- clk
            sram_0_external_interface_DQ            : inout std_logic_vector(15 downto 0) := (others => 'X'); -- DQ
            sram_0_external_interface_ADDR          : out   std_logic_vector(17 downto 0);                    -- ADDR
            sram_0_external_interface_LB_N          : out   std_logic;                                        -- LB_N
            sram_0_external_interface_UB_N          : out   std_logic;                                        -- UB_N
            sram_0_external_interface_CE_N          : out   std_logic;                                        -- CE_N
            sram_0_external_interface_OE_N          : out   std_logic;                                        -- OE_N
            sram_0_external_interface_WE_N          : out   std_logic                                         -- WE_N
        );
    end component BioLock;

    u0 : component BioLock
        port map (
            reset_reset_n                           => CONNECTED_TO_reset_reset_n,                           --                              reset.reset_n
            gpio_external_connection_export         => CONNECTED_TO_gpio_external_connection_export,         --           gpio_external_connection.export
            switches_external_connection_export     => CONNECTED_TO_switches_external_connection_export,     --       switches_external_connection.export
            green_leds_external_connection_export   => CONNECTED_TO_green_leds_external_connection_export,   --     green_leds_external_connection.export
            character_lcd_0_external_interface_DATA => CONNECTED_TO_character_lcd_0_external_interface_DATA, -- character_lcd_0_external_interface.DATA
            character_lcd_0_external_interface_ON   => CONNECTED_TO_character_lcd_0_external_interface_ON,   --                                   .ON
            character_lcd_0_external_interface_BLON => CONNECTED_TO_character_lcd_0_external_interface_BLON, --                                   .BLON
            character_lcd_0_external_interface_EN   => CONNECTED_TO_character_lcd_0_external_interface_EN,   --                                   .EN
            character_lcd_0_external_interface_RS   => CONNECTED_TO_character_lcd_0_external_interface_RS,   --                                   .RS
            character_lcd_0_external_interface_RW   => CONNECTED_TO_character_lcd_0_external_interface_RW,   --                                   .RW
            altpll_0_c0_clk                         => CONNECTED_TO_altpll_0_c0_clk,                         --                        altpll_0_c0.clk
            serial_external_connection_rxd          => CONNECTED_TO_serial_external_connection_rxd,          --         serial_external_connection.rxd
            serial_external_connection_txd          => CONNECTED_TO_serial_external_connection_txd,          --                                   .txd
            sdram_0_wire_addr                       => CONNECTED_TO_sdram_0_wire_addr,                       --                       sdram_0_wire.addr
            sdram_0_wire_ba                         => CONNECTED_TO_sdram_0_wire_ba,                         --                                   .ba
            sdram_0_wire_cas_n                      => CONNECTED_TO_sdram_0_wire_cas_n,                      --                                   .cas_n
            sdram_0_wire_cke                        => CONNECTED_TO_sdram_0_wire_cke,                        --                                   .cke
            sdram_0_wire_cs_n                       => CONNECTED_TO_sdram_0_wire_cs_n,                       --                                   .cs_n
            sdram_0_wire_dq                         => CONNECTED_TO_sdram_0_wire_dq,                         --                                   .dq
            sdram_0_wire_dqm                        => CONNECTED_TO_sdram_0_wire_dqm,                        --                                   .dqm
            sdram_0_wire_ras_n                      => CONNECTED_TO_sdram_0_wire_ras_n,                      --                                   .ras_n
            sdram_0_wire_we_n                       => CONNECTED_TO_sdram_0_wire_we_n,                       --                                   .we_n
            clk_clk                                 => CONNECTED_TO_clk_clk,                                 --                                clk.clk
            sram_0_external_interface_DQ            => CONNECTED_TO_sram_0_external_interface_DQ,            --          sram_0_external_interface.DQ
            sram_0_external_interface_ADDR          => CONNECTED_TO_sram_0_external_interface_ADDR,          --                                   .ADDR
            sram_0_external_interface_LB_N          => CONNECTED_TO_sram_0_external_interface_LB_N,          --                                   .LB_N
            sram_0_external_interface_UB_N          => CONNECTED_TO_sram_0_external_interface_UB_N,          --                                   .UB_N
            sram_0_external_interface_CE_N          => CONNECTED_TO_sram_0_external_interface_CE_N,          --                                   .CE_N
            sram_0_external_interface_OE_N          => CONNECTED_TO_sram_0_external_interface_OE_N,          --                                   .OE_N
            sram_0_external_interface_WE_N          => CONNECTED_TO_sram_0_external_interface_WE_N           --                                   .WE_N
        );

