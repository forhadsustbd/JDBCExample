create table version_table (
	serial		        NUMBER(10,0) not null,			                -- Serial for scrubbing, set from Master
	serial_version	    NUMBER(10,0) not null			                -- Version of the serial
);
create index p_vt on version_table (serial,serial_version);
commit;