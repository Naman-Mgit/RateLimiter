local key = KEYS[1]

local capacity = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local currentTime = tonumber(ARGV[3])

local tokens = redis.call('HGET', key, 'tokens')
local lastRefillTime = redis.call('HGET', key, 'lastRefillTime')

-- First request for this API key
if not tokens then
    tokens = capacity
    lastRefillTime = currentTime
else
    tokens = tonumber(tokens)
    lastRefillTime = tonumber(lastRefillTime)
end

-- Calculate elapsed time
local timePassed = currentTime - lastRefillTime

-- Calculate tokens to add
local tokensToAdd = (timePassed / 1000) * refillRate

-- Refill bucket
if tokensToAdd > 0 then

    tokens = math.min(
        capacity,
        tokens + tokensToAdd
    )

    lastRefillTime = currentTime
end

-- Try to consume one token
local allowed = 0

if tokens >= 1 then
    tokens = tokens - 1
    allowed = 1
end

-- Save bucket state
redis.call(
    'HSET',
    key,
    'tokens',
    tokens,
    'lastRefillTime',
    lastRefillTime
)

-- Return:
-- 1 = allowed
-- 0 = rejected
-- remaining tokens
return {allowed, tokens}