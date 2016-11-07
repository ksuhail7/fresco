CREATE DEFINER=`root`@`localhost` PROCEDURE `create_document`(
IN docid varchar(128),
IN storeid int,
IN docid_sha1 char(40),
IN version long,
IN filename varchar(128),
IN filesize bigint,
IN mimetype varchar(128),
IN sha1cksum char(40),
IN requestor varchar(64),
IN is_active boolean,
OUT docref int
)
BEGIN
	DECLARE `_rollback` BOOL DEFAULT 0;
    declare creation_time datetime default now();
    declare docRefKey varchar(20) default 'documentRef';
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET `_rollback` = 1;
	if is_active is null then
		select is_active = true;
	end if;

    start transaction;
    if not exists (select docref from document where docid = docid) then
		call gen_id(docRefKey, docref);
        insert into document
    (docref, docid, storeid, creation_date, created_by, update_date, updated_by, docid_sha1, is_active)
    values
    (docref, docid, storeid, creation_time, requestor,
    creation_time,
    requestor,
    docid_sha1,
    is_active);
    else
		select docref = docref from document where docid = docid;
    end if;


    insert into document_version
    (docref, version, filename, filesize_in_bytes, mimetype,
    sha1_checksum, creation_date, created_by, update_date, updated_by, is_active)
    values
    (docref,
    version,
    filename,
    filesize,
    mimetype,
    sha1cksum,
    creation_time,
    requestor,
    creation_time,
    requestor,
    is_active);
    IF `_rollback` THEN
        ROLLBACK;
    ELSE
        COMMIT;
    END IF;
END;