CREATE TABLE bportals_portals (
	`id` VARCHAR(32) NOT NULL,
	`pos1` VARCHAR(128) NOT NULL,
	`pos2` VARCHAR(128) NOT NULL,
	`exit` VARCHAR(128) NOT NULL,
	`teleport` VARCHAR(128) NOT NULL,
	UNIQUE (id)
);