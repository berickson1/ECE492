-- g9 - BioLock
-- Top level system file

library ieee;

use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.vital_primitives.all;
use work.DE2_CONSTANTS.all;

entity BioLock is
        port (
		-- Reset and Clock
		KEY		: in std_logic_vector (0 downto 0);
		CLOCK_50	: in std_logic;


		-- Off Chip
		GPIO_0		: inout std_logic_vector(9 downto 0);

		-- Switches
		SW		: in std_logic_vector (17 downto 0); 

		-- Green LEDs On Board
		LEDG		: out DE2_LED_GREEN;

		-- LCD On Board
		LCD_BLON	: out std_logic;
		LCD_ON		: out std_logic;
		LCD_DATA	: inout DE2_LCD_DATA_BUS;
		LCD_RS		: out std_logic;
		LCD_EN		: out std_logic;
		LCD_RW		: out std_logic;

		-- SDRAM On Board
		DRAM_ADDR	: out DE2_SDRAM_ADDR_BUS;
		DRAM_BA_0	: out std_logic;
		DRAM_BA_1	: out std_logic;
		DRAM_CAS_N	: out std_logic;
		DRAM_CKE	: out std_logic;
		DRAM_CLK	: out std_logic;
		DRAM_CS_N	: out std_logic;
		DRAM_DQ		: inout DE2_SDRAM_DATA_BUS;
		DRAM_LDQM	: out std_logic;
		DRAM_UDQM	: out std_logic;
		DRAM_RAS_N	: out std_logic;
		DRAM_WE_N	: out std_logic;

		-- SRAM On Board
		SRAM_ADDR	: out DE2_SRAM_ADDR_BUS;
		SRAM_DQ		: inout DE2_SRAM_DATA_BUS;
		SRAM_WE_N	: out std_logic;
		SRAM_OE_N	: out std_logic;
		SRAM_UB_N	: out std_logic;
		SRAM_LB_N	: out std_logic; 
		SRAM_CE_N	: out std_logic;
		
		-- Ethernet
		ENET_CLK		:	out std_logic;
		ENET_CMD		:	out std_logic;
		ENET_CS_N	:	out std_logic;
		ENET_INT		:	in std_logic;
		ENET_RD_N	: 	out std_logic;
		ENET_WR_N	:	out std_logic;
		ENET_RST_N	:	out std_logic;
		ENET_DATA	: 	inout std_logic_vector(15 downto 0)
	
	);
end BioLock;


architecture structure of BioLock is

	component nios_system is
	port (
	         clk_clk                                 : in    std_logic                     := 'X';             -- clk
            reset_reset_n                           : in    std_logic                     := 'X';             -- reset_n

            gpio_external_connection_export         : out   std_logic_vector(7 downto 0);                                        -- export

            switches_external_connection_export     : in    std_logic_vector(17 downto 0) := (others => 'X'); -- export

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
            
            sram_0_external_interface_DQ            : inout std_logic_vector(15 downto 0) := (others => 'X'); -- DQ
            sram_0_external_interface_ADDR          : out   std_logic_vector(17 downto 0);                    -- ADDR
            sram_0_external_interface_LB_N          : out   std_logic;                                        -- LB_N
            sram_0_external_interface_UB_N          : out   std_logic;                                        -- UB_N
            sram_0_external_interface_CE_N          : out   std_logic;                                        -- CE_N
            sram_0_external_interface_OE_N          : out   std_logic;                                        -- OE_N
            sram_0_external_interface_WE_N          : out   std_logic;                                         -- WE_N
			
			dm9000a_if_0_s1_export_DATA             : inout std_logic_vector(15 downto 0) := (others => 'X'); -- DATA
            dm9000a_if_0_s1_export_CMD              : out   std_logic;                                        -- CMD
            dm9000a_if_0_s1_export_RD_N             : out   std_logic;                                        -- RD_N
            dm9000a_if_0_s1_export_WR_N             : out   std_logic;                                        -- WR_N
            dm9000a_if_0_s1_export_CS_N             : out   std_logic;                                        -- CS_N
            dm9000a_if_0_s1_export_RST_N            : out   std_logic;                                        -- RST_N
            dm9000a_if_0_s1_export_INT              : in    std_logic                     := 'X';             -- INT
            dm9000a_if_0_s1_export_CLK              : out   std_logic    
        );
    end component nios_system;

	-- signals to match provided IP core to specific SDRAM chip of our system
	signal BA	: std_logic_vector (1 downto 0);
	signal DQM	: std_logic_vector (1 downto 0);
begin
	DRAM_BA_1 <= BA(1);
	DRAM_BA_0 <= BA(0);

	DRAM_UDQM <= DQM(1);
	DRAM_LDQM <= DQM(0);

    u0 : component nios_system
        port map (
            reset_reset_n                           => KEY(0),                           		     -- reset.reset_n

            gpio_external_connection_export         => GPIO_0(7 downto 0),         				     -- gpio_external_connection.export

            switches_external_connection_export     => SW(17 downto 0),     				     -- switches_external_connection.export

            green_leds_external_connection_export   => LEDG,   						     -- green_leds_external_connection.export

            character_lcd_0_external_interface_DATA => LCD_DATA, 					     -- character_lcd_0_external_interface.DATA
            character_lcd_0_external_interface_ON   => LCD_ON,  					     --                                   .ON
            character_lcd_0_external_interface_BLON => LCD_BLON, 					     --                                   .BLON
            character_lcd_0_external_interface_EN   => LCD_EN,   					     --                                   .EN
            character_lcd_0_external_interface_RS   => LCD_RS,					             --                                   .RS
            character_lcd_0_external_interface_RW   => LCD_RW, 						     --                                   .RW

            altpll_0_c0_clk                         => DRAM_CLK, 		                             --                        altpll_0_c0.clk

            serial_external_connection_rxd          => GPIO_0(8),				             --         serial_external_connection.rxd
            serial_external_connection_txd          => GPIO_0(9), 				             --                                   .txd

            sdram_0_wire_addr                       => DRAM_ADDR, 		 	                     --                       sdram_0_wire.addr
            sdram_0_wire_ba                         => BA,                         			     --                                   .ba
            sdram_0_wire_cas_n                      => DRAM_CAS_N,			                     --                                   .cas_n
            sdram_0_wire_cke                        => DRAM_CKE,			                     --                                   .cke
            sdram_0_wire_cs_n                       => DRAM_CS_N,               			     --                                   .cs_n
            sdram_0_wire_dq                         => DRAM_DQ,  			                     --                                   .dq
            sdram_0_wire_dqm                        => DQM,   				                     --                                   .dqm
            sdram_0_wire_ras_n                      => DRAM_RAS_N,			                     --                                   .ras_n
            sdram_0_wire_we_n                       => DRAM_WE_N, 			                     --                                   .we_n

            clk_clk                                 => CLOCK_50,                                	     --                                clk.clk

            sram_0_external_interface_DQ            => SRAM_DQ,					             --          sram_0_external_interface.DQ
            sram_0_external_interface_ADDR          => SRAM_ADDR,				             --                                   .ADDR
            sram_0_external_interface_LB_N          => SRAM_LB_N,				             --                                   .LB_N
            sram_0_external_interface_UB_N          => SRAM_UB_N,				             --                                   .UB_N
            sram_0_external_interface_CE_N          => SRAM_CE_N,				             --                                   .CE_N
            sram_0_external_interface_OE_N          => SRAM_OE_N,				             --                                   .OE_N
            sram_0_external_interface_WE_N          => SRAM_WE_N,				             --                                   .WE_N
			
			dm9000a_if_0_s1_export_DATA             => ENET_DATA,             			--             dm9000a_if_0_s1_export.DATA
            dm9000a_if_0_s1_export_CMD              => ENET_CMD,              --                                   .CMD
            dm9000a_if_0_s1_export_RD_N             => ENET_RD_N,             --                                   .RD_N
            dm9000a_if_0_s1_export_WR_N             => ENET_WR_N,             --                                   .WR_N
            dm9000a_if_0_s1_export_CS_N             => ENET_CS_N,             --                                   .CS_N
            dm9000a_if_0_s1_export_RST_N            => ENET_RST_N,            --                                   .RST_N
            dm9000a_if_0_s1_export_INT              => ENET_INT,              --                                   .INT
            dm9000a_if_0_s1_export_CLK              => ENET_CLK               --                                   .CLK

        );

end structure;

library ieee;

--DE2 Constants

use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.vital_primitives.all;

package DE2_CONSTANTS is
	subtype DE2_LCD_DATA_BUS	is std_logic_vector(7 downto 0);
	
	subtype DE2_LED_GREEN	is std_logic_vector(7 downto 0);

	subtype DE2_SRAM_ADDR_BUS	is std_logic_vector(17 downto 0);
	subtype DE2_SRAM_DATA_BUS	is std_logic_vector(15 downto 0);

	subtype DE2_SDRAM_ADDR_BUS	is std_logic_vector(11 downto 0);
	subtype DE2_SDRAM_DATA_BUS is std_logic_vector(15 downto 0);

end DE2_CONSTANTS;

