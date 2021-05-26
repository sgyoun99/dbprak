CREATE TABLE public.shop
(
    shop_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    street character varying(255) COLLATE pg_catalog."default" NOT NULL,
    zip character varying(5) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT pk_shop PRIMARY KEY (shop_name, street, zip),
    CONSTRAINT shop_name UNIQUE (shop_name),
    CONSTRAINT street UNIQUE (street),
    CONSTRAINT zip UNIQUE (zip)
)

CREATE TABLE public.productgroup
(
    pgroup character varying(10) NOT NULL,
    PRIMARY KEY (pgroup)
);

CREATE TABLE public.item
(
    item_id character varying(10) COLLATE pg_catalog."default" NOT NULL,
    title text COLLATE pg_catalog."default" NOT NULL,
    salesranking integer,
    image text COLLATE pg_catalog."default",
    pgroup character varying(10) COLLATE pg_catalog."default" NOT NULL,
    rating numeric(1,1),
    CONSTRAINT pk_item PRIMARY KEY (item_id),
    CONSTRAINT fk_item FOREIGN KEY (pgroup)
        REFERENCES public.productgroup (pgroup) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

CREATE TABLE public."item-shop"
(
    item_id character varying(10) COLLATE pg_catalog."default" NOT NULL,
    shop_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    street character varying(255) COLLATE pg_catalog."default" NOT NULL,
    zip character varying(5) COLLATE pg_catalog."default" NOT NULL,
    price integer,
    mult numeric(3,2),
    currency character(3) COLLATE pg_catalog."default",
    availabilty boolean,
    state character varying(10) COLLATE pg_catalog."default",
    CONSTRAINT "pk_item-shop" PRIMARY KEY (item_id, shop_name, street, zip),
    CONSTRAINT fk_item_id FOREIGN KEY (item_id)
        REFERENCES public.item (item_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT fk_shop FOREIGN KEY (shop_name, zip, street)
        REFERENCES public.shop (shop_name, zip, street) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE NO ACTION
        NOT VALID
)





-- ends here!!
ALTER TABLE public.shop
    OWNER to postgres;
















--alter sample
ALTER TABLE public.author
    ADD CONSTRAINT const1 FOREIGN KEY (test)
    REFERENCES public.author (test2) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE NO ACTION;
--샘플
CREATE TABLE `sample_db`.`event_list` (
  `event_id` VARCHAR(32) NOT NULL,
  `event_type` TINYINT UNSIGNED NOT NULL,
  `bg_image_url` TEXT,
  `target` text,
  `title` VARCHAR(128) NOT NULL,
  `desc` VARCHAR(256) NOT NULL,
  `start_datetime` TIMESTAMP NOT NULL,
  `end_datetime` TIMESTAMP NOT NULL,
  `last_update` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`event_id`,`event_type`),
  INDEX `idx_end_date` (`end_datetime`)
  );